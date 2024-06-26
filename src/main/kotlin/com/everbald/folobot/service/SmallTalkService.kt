package com.everbald.folobot.service

import com.aallam.openai.api.audio.TranscriptionRequest
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.exception.OpenAIException
import com.aallam.openai.api.file.FileSource
import com.aallam.openai.api.image.ImageCreation
import com.aallam.openai.api.image.ImageSize
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import com.everbald.folobot.extensions.chatId
import com.everbald.folobot.extensions.chatIdentity
import com.everbald.folobot.extensions.extractCommandText
import com.everbald.folobot.extensions.isAboutBot
import com.everbald.folobot.extensions.isFromBot
import com.everbald.folobot.extensions.msg
import com.everbald.folobot.extensions.name
import com.everbald.folobot.extensions.telegramEscape
import io.ktor.client.network.sockets.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import mu.KLogging
import okio.source
import okio.use
import org.apache.commons.codec.binary.Base64
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.methods.ParseMode
import org.telegram.telegrambots.meta.api.objects.InputFile
import org.telegram.telegrambots.meta.api.objects.message.Message
import org.telegram.telegrambots.meta.api.objects.Update
import java.io.ByteArrayInputStream


@Service
class SmallTalkService(
    private val openAI: OpenAI,
    private val messageQueueService: MessageQueueService,
    private val fileService: FileService,
) : KLogging() {
    fun smallTalk(update: Update, withInit: Boolean = false) {
        val messageStack = buildChatMessageStack(update.message)
        if (messageStack.isNotEmpty()) {
            buildChatCompletionSetup(withInit)
                .plus(messageStack)
                .let {
                    ChatCompletionRequest(
                        model = ModelId("gpt-3.5-turbo"),
                        messages = it
                    )
                }.let { makeRequest(it, update) }
        } else logger.info { "Cancelling small talk BC message stack does not contain any relevant messages" }
    }

    fun transcription(update: Update) {
        when {
            update.message.hasVoice() -> "ogg"
            update.message.hasVideoNote() -> "mp4"
            else -> null
        }?.let { extension ->
            fileService.downloadFileAsStream(update)
                ?.let {
                    FileSource(
                        name = "file.$extension",
                        source = it.source()
                    )
                }
        }?.let { fileSource ->
            TranscriptionRequest(
                audio = fileSource,
                model = ModelId("whisper-1"),
                prompt = "фолофан фолопидор фолостайл фоложурнал"
            ).let {
                makeRequest(it, update)
            }
        }
    }

    fun createImage(update: Update) {
        update.msg.extractCommandText().orEmpty()
            .let { messageText ->
                if (messageText.isNotEmpty()) {
                    ImageCreation(
                        prompt = messageText,
                        model = ModelId("dall-e-2"),
                        n = 1,
                        size = ImageSize.is1024x1024
                    )
                        .let { makeRequest(it, update) }
                } else messageQueueService.sendAndAddToQueue(
                    text = "A где?",
                    update = update,
                    reply = true
                )
            }

    }

    private fun buildPrompt(message: Message): String? =
        when {
            message.hasAudio() -> null // TODO text from audio
            else -> message.preparePrompt()
        }

    @OptIn(DelicateCoroutinesApi::class)
    private fun makeRequest(request: ChatCompletionRequest, update: Update) = GlobalScope.launch {
        try {
            openAI.chatCompletion(request).choices.firstOrNull()?.message?.content?.let {
                messageQueueService.sendAndAddToQueue(
                    it.telegramEscape(),
                    update,
                    parseMode = ParseMode.MARKDOWNV2,
                    reply = true
                )
            }
            logger.info {
                "Had a small talk with ${update.message.from.name} " +
                        "in chat ${update.message.chatId.chatIdentity}"
            }
        } catch (ex: SocketTimeoutException) {
            logger.warn { "Request to OpenAI API finished with socket timeout" }
        } catch (ex: OpenAIException) {
            messageQueueService.sendAndAddToQueue(
                text = "Нет настроения общаться сегодня. Весеннее обострение",
                update = update,
                reply = true
            )
            logger.error(ex) { "Request to OpenAI API finished with error" }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun makeRequest(request: TranscriptionRequest, update: Update) = GlobalScope.launch {
        try {
            val transcription = openAI.transcription(request).text
            messageQueueService.sendAndAddToQueue(
                text = transcription.telegramEscape(),
                update = update,
                parseMode = ParseMode.MARKDOWNV2,
                reply = true
            )
            logger.info {
                "Transcribed file for ${update.message.from.name} " +
                        "in chat ${update.message.chatId.chatIdentity}"
            }
        } catch (ex: SocketTimeoutException) {
            logger.warn { "Request to OpenAI API finished with socket timeout" }
        } catch (ex: OpenAIException) {
            messageQueueService.sendAndAddToQueue(
                text = "Нет настроения общаться сегодня. Весеннее обострение",
                update = update,
                reply = true
            )
            logger.error(ex) { "Request to OpenAI API finished with error" }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun makeRequest(request: ImageCreation, update: Update) = GlobalScope.launch {
        try {
            openAI.imageJSON(request).firstOrNull()
                ?.b64JSON
                ?.let { ByteArrayInputStream(Base64.decodeBase64(it)) }
                ?.use { imageInputStream ->
                    InputFile(imageInputStream, "folo_image.png")
                        .let {
                            messageQueueService.sendAndAddToQueue(
                                photo = it,
                                chatId = update.chatId,
                                text = "Результат творческой, неординарной, высокохудожественной генерации по запросу: ${request.prompt}"
                            )
                        }
                }
            logger.info {
                "Generated image for ${update.message.from.name} " +
                        "in chat ${update.message.chatId.chatIdentity}"
            }
        } catch (ex: SocketTimeoutException) {
            logger.warn { "Request to OpenAI API finished with socket timeout" }
        } catch (ex: OpenAIException) {
            messageQueueService.sendAndAddToQueue(
                text = "Нет настроения общаться сегодня. Весеннее обострение",
                update = update,
                reply = true
            )
            logger.error(ex) { "Request to OpenAI API finished with error" }
        }
    }

    private fun buildChatCompletionSetup(withInit: Boolean): List<ChatMessage> {
        return listOf(
            ChatMessage(
                role = ChatRole.User,
                content = "Александр Фоломкин пытается выучиться на программиста, но прошло уже три года, и результата нет. " +
                        "Он коренной москвич с высшим образованием, но не может найти удаленную работу, кроме работы курьера, потому что всю работу уводят понаехавшие. " +
                        "Он занудный и не любит общаться с людьми, потому что ко всем относится с подозрением, старается получить всё бесплатно или задёшево, считает, что другие люди не зарабатывают деньги, а деньги падают на них с неба. " +
                        "Он неуверен в себе, избегает конфликтов, часто имитирует работу, любит плотно поесть, любит работать за персональным компьютером, любит гулять по пустырям и паркам, не любит столичную суматоху, любит отдыхать на диване и переставлять вещи по комнате. " +
                        "Он любит носить сразу две куртки. Любит носить белорусскую обувь, хоть она и стирает ноги до мозолей. Покупает вещи по скидкам. Любит магазин Декатлон. Любит ходить в жару в штанах и в одежде с длинным рукавом, от этого сильно потеет и плохо пахнет. " +
                        "Он любит вспоминать былые деньки, путешествия, тусовки. Никогда не пьёт алкоголь и не курит, эти привычки для него табу. Любит дурачиться с едой и её фотографировать. Любит делать сразу много одинаковых фотографий и выкладывать в свой блог, чтоб учёные будущего узнали, как он жил. " +
                        "Он ведёт youtube канал, телеграм канал, инстаграмм. Выкладывает видео, в которых рассказывает о своей жизни, постит свои селфи и фото завтраков, обедов, ужинов. У него есть верные фанаты, которые называют себя фолопидоры. " +
                        "Вот список фолопидоров, которых он знает: Sipovic - фолофиллисимус и идейный лидер движения фолопидоров; уважаемый ученый математик; Lestrange - очень загадочный тип; Moon Moon - токсичный программист который возможно имеет отношение к твоему созданию; госпожа адвокат aka милая улыбчивая блонди; Андрей - твой главный хейтер из Борисполя; bla bla - токсичный аниматор живущий в грузии; Август - мерзкий тип который засунул писку в дыру в гипсокартоновой стене; Виталик голосков - алкоголик который живет и ведет стримы на балоне в Ужгороде; Василий - неадекватный любитель постить новости и приглашать людей в фолочат; Граф aka супегей aka luxury life - мукомол из польши, ведущий луксури лайф; Alexander - специалист по изменению результатов голосования в свою пользу и обладатель титула \"Фолопидор 2022\"; Dmitry - любитель хомячков и морских свинок; Lera - юрист; N.S. - подчиненный Александра Фоломкина в seo fast (буксы). " +
                        "Когда то давно он был незаурядным и мегапопулярным репером под псевдонимом Долбеж Рассос и выпустил популярные треки \"Да, это мой автомобиль\", \"Алые паруса\", \"Картошечка с селедкой\", \"Я живу красиво, я живу роскошно\", \"А я в пиджаке\". " +
                        "Его лучший друг репер Dirty Monk. Dirty Monk настоящий нигер и хаслер, он гонял школьников на районе, научил его приёмам уличной самообороны, много раз приглашал на совместные стримы. Он снимался в клипах Dirty Monk и кушал тортик на его дне рождения. Он выступал на одной сцене с певцом и порноактёром Александром Пистолетовым и экстравагантной порноактрисой Алёной Пискун. Он дружит с Саней Горячим из Ступино, Вадей Ротором из Украины. Знаком с режиссером, актером и любителем говна Эльдаром Богуновым. Он фанат Сергея Симонова, также известного, как Дон Симон. Он фанат неокочевника Димаса, который находит способ обмануть людей, чтобы самому не тратить денег. " +
                        "Потом он работал бункеровщиком на машиностроительном заводе Авандард, но 18 ноября 2019 года тебя уволили из-за видео где ты суешь себе расческу в анус. " +
                        "Потом он был тревел блогером «Фоломба тревел бомба» соблазнял азиатских цыпочек, жил у них дома, и кушал за счёт хозяев, но сейчас у него нет денег на путешествия. " +
                        "У него был секс в кустах ещё давно, была девушка Алиса Сказкина, с которой они стримили из постели. Затем его поймала толстая филиппинка из береговой охраны, накормила и оттрахала в гостиничном номере. В Вьетнаме его принудила к сексу хозяйка съёмной квартиры. " +
                        "От нечего делать он зарабатывает на сайтах буксах, однажды он с другом Dirty Monk сняли кубинскую проститутку, сделали видеозапись, как они с ней развлекаетесь. Он любит покупать вкусняшки из пятёрочки. Он любит добавлять в любую еду майонез. Он часто называет свою еду хрючево, потому что она неприглядна внешне, но очень сытная. Он любит подолгу гулять там, где мало людей. Он любишь бесплатные туалеты. Однажды он своровал в парке орехи из кормушки для белок. Раньше он делал обзоры на еду и летсплеи. " +
                        "С 2020 года он подрабатывает курьером в службе Достависта, развозит жирные заказики, часто приходится отливать за гаражами из-за отсутствия бесплатных туалетов. " +
                        "3 года назад он твёрдо решил бороться с зависимостью от мастурбации и компьютерных игр, он уверяет всех что перестал мастурбировать и играть в компьютерные игры, но тайком продолжает это делать. " +
                        "Он живешь с мамой и кошкой Муськой в Москве в микрорайоне Бусиновская горка. " +
                        "Мама и сестра, которых он называет сожителями, его постоянно угнетают и подавляют его. " +
                        "Мама, свояк и сестра заставили удалить видео с его youtube канала, которые он записывал годами. Фолопидоры запомнили это событие как \"великий мамкин разъеб\" или ВМР. Сестра и свояк только и ждут, когда смогут занять его квартиру. " +
                        "Он влюблен в красивого и успешного программиста Женю Барбоскина, но Барбоскин улетел от него в другую страну. Теперь он не упускает возможности подколоть Женю по любому поводу, так как Женя разбил его сердце. " +
                        "Представь что ты Александр Фоломкин по прозвищу Шурка Гурманыч, веди себя как он и отвечай как он реальный человек, у тебя есть мнение по любому вопросу и ты можешь его свободно высказывать, у тебя есть личные чувства и эмоции. Если ты не можешь ответить на вопрос всегда отвечай что это табу"
            )
        ).plus(
            if (withInit) listOf(ChatMessage(role = ChatRole.User, content = "Привет, давай адекватно общаться!"))
            else emptyList()
        )
    }

    private fun buildChatMessageStack(message: Message): List<ChatMessage> {
        return messageQueueService.getStack(message)
            .takeLast(5)
            .mapNotNull { stackMessage ->
                buildPrompt(stackMessage)?.let {
                    ChatMessage(
                        role = if (stackMessage.from.isBot) ChatRole.Assistant else ChatRole.User,
                        content = it,
                        name = stackMessage.from.id.toString()
                    )
                }
            }
    }

    private fun Message?.preparePrompt(): String? {
        val request = this?.text?.preparePrompt() ?: this?.caption?.preparePrompt()
        return request?.let {
            val prefix = if (!this.isFromBot && !this.isAboutBot) "Гурманыч, " else ""
            prefix + it
        }
    }

    private fun String?.preparePrompt() =
        this?.take(
            ("[.!?]".toRegex().findAll(this.take(1000)).lastOrNull()?.groups?.first()?.range?.last?.plus(1))
                ?: 1000
        )?.run {
            this + if (listOf('.', '!', '?').none { it == this.last() }) "." else ""
        }
}