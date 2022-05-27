package com.telegram.folobot;

import com.telegram.folobot.domain.*;
import com.telegram.folobot.constants.ActionsEnum;
import com.telegram.folobot.constants.BotCommandsEnum;
import com.telegram.folobot.constants.NumTypeEnum;
import com.telegram.folobot.constants.VarTypeEnum;
import com.telegram.folobot.repos.FoloPidorRepo;
import com.telegram.folobot.repos.FoloUserRepo;
import com.telegram.folobot.repos.FoloVarRepo;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.ActionType;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.ForwardMessage;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatMember;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendSticker;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDate;
import java.time.Period;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.telegram.folobot.ChatId.*;
import static com.telegram.folobot.Utils.printExeptionMessage;

//TODO разбить на сервисы, очень большой

@Service
@RequiredArgsConstructor
public class Bot extends TelegramWebhookBot { //TODO библиотека sl4j для логгирования, sonar lint plugin для проверки
    @Value("${bot.username}")
    @Getter
    private String botUsername;
    @Value("${bot.token}")
    @Getter
    private String botToken;
    @Value("${bot.path}")
    @Getter
    private String botPath;

    private final FoloPidorRepo foloPidorRepo;
    private final FoloUserRepo foloUserRepo;
    private final FoloVarRepo foloVarRepo;

    /**
     * Пришел update от Telegram
     *
     * @param update {@link Update}
     * @return {@link BotApiMethod}
     */
    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        if (update.hasMessage()) {
            //Добавление фолопользователя в бд
            addFoloUser(update);

            //Пересылка личных сообщений в спецчат
            forwardPrivate(update);

            //Действие в зависимости от содержимого update
            return onAction(getAction(update), update);
        }
        return null;
    }

    /**
     * Залоггировать пользователя
     *
     * @param update {@link Update}
     */
    private void addFoloUser(Update update) {
        Message message = update.getMessage();
        if (!isChatMessage(message)) {
            User user = message.getFrom();
            if (user == null && !message.getNewChatMembers().isEmpty()) {
                user = message.getNewChatMembers().get(0);
            }
            if (user != null) {
                // Фолопользователь
                FoloUser foloUser = foloUserRepo.findById(user.getId())
                        .orElse(new FoloUser(user.getId()));
                foloUser.setName(getUserName(user));
                foloUserRepo.save(foloUser);
                // И фолопидор
                if (!message.isUserMessage() && getFoloPidor(message.getChatId(), user.getId()).isNew()) {
                    foloPidorRepo.save(new FoloPidor(new FoloPidorId(message.getChatId(), user.getId())));
                }
            }
        }
    }

    /**
     * Определение является ли сообщение постом канала
     *
     * @param message {@link  Message}
     * @return да/нет
     */
    private boolean isChatMessage(Message message) {
        try {
            return message.getIsAutomaticForward();
        } catch (NullPointerException e) { //TODO очень плохо убрать
            return false;
        }
    }

    /**
     * Определяет действие на основе приходящего Update
     *
     * @param update {@link Update} пробрасывается из onUpdateReceived
     * @return {@link ActionsEnum}
     */
    private ActionsEnum getAction(Update update) {
        // Не содержит сообщения
        if (!update.hasMessage()) {
            return ActionsEnum.UNDEFINED;
        }

        Message message = update.getMessage();

        // Команда
        if (message.hasText()) {
            if (message.getChat().isUserChat() && message.getText().startsWith("/") ||
                    !message.getChat().isUserChat() && message.getText().startsWith("/") &&
                            message.getText().endsWith("@" + botUsername)) {
                return ActionsEnum.COMMAND;
            }
        }
        // Личное сообщение
        if (message.isUserMessage()) {
            return ActionsEnum.USERMESSAGE;
        }
        // Ответ на обращение
        if (message.hasText()) {
            if (message.getText().toLowerCase().contains("гурманыч") ||
                    message.getText().toLowerCase().contains(botUsername.toLowerCase())) {
                return ActionsEnum.REPLY;
            }
        }
        // Пользователь зашел в чат
        if (!message.getNewChatMembers().isEmpty()) {
            return ActionsEnum.USERNEW;
        }
        // Пользователь покинул чат
        if (Objects.nonNull(message.getLeftChatMember())) {
            return ActionsEnum.USERLEFT;
        }
        return ActionsEnum.UNDEFINED;
    }

    /**
     * Действие в зависимости от содержимого {@link Update}
     *
     * @param action {@link ActionsEnum}
     * @param update пробрасывается из onUpdateReceived
     * @return {@link BotApiMethod}
     */
    private BotApiMethod<?> onAction(ActionsEnum action, Update update) {
        if (action != null && action != ActionsEnum.UNDEFINED) {

            switch (action) {
                case COMMAND:
                    return onCommand(update);
                case USERMESSAGE:
                    return onUserMessage(update);
                case REPLY:
                    return onReply(update);
                case USERNEW:
                    return onUserNew(update);
                case USERLEFT:
                    return onUserLeft(update);
            }
        }
        return null;
    }

    /**
     * Выполнение команды
     *
     * @param update {@link Update}
     * @return {@link BotApiMethod}
     */
    private BotApiMethod<?> onCommand(Update update) {
        BotCommandsEnum command = BotCommandsEnum.valueOfLabel(update.getMessage().getText().split("@")[0]);
        if (command != null) {
            sendChatTyping(update);
            switch (command) { // TODO обрабатывать /start
                case SILENTSTREAM:
                    sendSticker(getRandomSticker(), update); //TODO возвращать BotApiMethod
                    break;
                case FREELANCE:
                    return frelanceTimer(update);
                case NOFAP:
                    return nofapTimer(update);
                case FOLOPIDOR:
                    return foloPidor(update);
                case FOLOPIDORTOP:
                    return foloPidorTop(update);
            }
        }
        return null;
    }

    /**
     * Подсчет времени прошедшего с дня F
     *
     * @param update {@link Update}
     */
    private BotApiMethod<?> frelanceTimer(Update update) {
        return buildMessage("18 ноября 2019 года я уволился с завода по своему желанию.\n" +
                "С тех пор я стремительно вхожу в IT вот уже\n*" +
                Utils.getDateText(Period.between(LocalDate.of(2019, 11, 18), LocalDate.now())) +
                "*!", update);
    }

    private BotApiMethod<?> nofapTimer(Update update) {
        return buildMessage("Для особо озабоченных в десятый раз повторяю тут Вам, " +
                "что я с Нового 2020 Года и до сих пор вот уже *" +
                Utils.getDateText(Period.between(LocalDate.of(2020, 1, 1), LocalDate.now())) +
                "* твёрдо и уверенно держу \"Но Фап\".", update);
    }


    /**
     * Определяет фолопидора дня. Если уже определен показывает кто
     *
     * @param update {@link Update}
     * @return {@link BotApiMethod}
     */
    private BotApiMethod<?> foloPidor(Update update) {
        Long chatid = update.getMessage().getChatId();
        if (!update.getMessage().isUserMessage()) {
            //Определяем дату и победителя предыдущего запуска
            LocalDate lastDate = getLastFolopidorDate(chatid);
            Long lastWinner = getLastFolopidorWinner(chatid);

            //Определяем либо показываем победителя
            if (Objects.isNull(lastWinner) || lastDate.isBefore(LocalDate.now())) {
                //Выбираем случайного
                FoloPidor folopidor = getFoloPidor(chatid);

                //Обновляем счетчик
                folopidor.setScore(folopidor.getScore() + 1);
                foloPidorRepo.save(folopidor);

                //Обновляем текущего победителя
                setLastFolopidorWinner(chatid, folopidor.getFoloUser().getUserId());
                setLastFolopidorDate(chatid, LocalDate.now());

                //Поздравляем
                sendMessage(Text.getSetup(), update);
                sendMessage(Text.getPunch(getFoloUserName(folopidor)), update);
            } else {
                return buildMessage("Фолопидор дня уже выбран, это *" +
                        getFoloUserName(getFoloPidor(chatid, lastWinner)) +
                        "*. Пойду лучше лампово попержу в диван", update);
            }

        } else {
            return buildMessage("Для меня вы все фолопидоры, " +
                    getFoloUserName(update.getMessage().getFrom()), update, true);
        }
        return null;
    }

    /**
     * Дата последнего определения фолопидора
     *
     * @param chatid ID чата
     * @return {@link LocalDate}
     */
    public LocalDate getLastFolopidorDate(Long chatid) { //TODO подумать как упростить
        Optional<FoloVar> foloVarEntity = foloVarRepo
                .findById(new FoloVarId(chatid, VarTypeEnum.LAST_FOLOPIDOR_DATE.name()));
        return foloVarEntity.isPresent()
                ? LocalDate.parse(foloVarEntity.get().getValue())
                : LocalDate.parse("1900-01-01");
    }

    /**
     * Сохранить дату последнего определения фолопидора
     *
     * @param chatid ID чата
     * @param value  Дата
     */
    public void setLastFolopidorDate(Long chatid, LocalDate value) {
        foloVarRepo.save(new FoloVar(
                new FoloVarId(chatid, VarTypeEnum.LAST_FOLOPIDOR_DATE.name()), value.toString()));
    }

    /**
     * Последний фолопидор
     *
     * @param chatid ID чата
     * @return {@link Long} userid
     */
    public Long getLastFolopidorWinner(Long chatid) {
        Optional<FoloVar> foloVarEntity = foloVarRepo
                .findById(new FoloVarId(chatid, VarTypeEnum.LAST_FOLOPIDOR_USERID.name()));
        return foloVarEntity.isPresent()
                ? Long.parseLong(foloVarEntity.get().getValue())
                : null;
    }

    /**
     * Сохранить последнего фолопидора
     *
     * @param chatid ID чата
     * @param value  {@link Long} userid
     */
    public void setLastFolopidorWinner(Long chatid, Long value) {
        foloVarRepo.save(new FoloVar(
                new FoloVarId(chatid, VarTypeEnum.LAST_FOLOPIDOR_USERID.name()), Long.toString(value)));
    }

    /**
     * Получение объекта фолопидора. Если не найден - возвращает пустого фолопидора
     *
     * @param chatid ID чата
     * @param userid ID фолопидора
     * @return {@link FoloPidor}
     */
    public FoloPidor getFoloPidor(Long chatid, Long userid) {
        return foloPidorRepo.findById(new FoloPidorId(chatid, userid))
                .orElse(FoloPidor.createNew(new FoloPidorId(chatid, userid)));
    }

    /**
     * Выбор случайного фолопидора
     *
     * @param chatid ID чата
     * @return {@link FoloPidor}
     */
    public FoloPidor getFoloPidor(Long chatid) {
        //Получаем список фолопидоров для чата
        List<FoloPidor> foloPidorEntities = foloPidorRepo.findByIdChatId(chatid);
        //Выбираем случайного
        return foloPidorEntities.get(new SplittableRandom().nextInt(foloPidorEntities.size()));
    }

    /**
     * Показывает топ фолопидоров
     *
     * @param update {@link Update}
     * @return {@link BotApiMethod}
     */
    private BotApiMethod<?> foloPidorTop(Update update) {
        if (!update.getMessage().isUserMessage()) {
            StringJoiner top = new StringJoiner("\n").add("Топ 10 *фолопидоров*:\n");
//            List<FoloPidor> foloPidorEntities =
//                    foloPidorRepo.findByIdChatId(update.getMessage().getChatId()).stream()
//                            .sorted(Comparator.comparingInt(FoloPidor::getScore).reversed())
//                            .filter(FoloPidor::hasScore)
//                            .limit(10)
//                            .toList();
            List<FoloPidor> foloPidors = foloPidorRepo.
                    findTop10ByIdChatIdOrderByScoreDesc(update.getMessage().getChatId())
                    .stream().filter(FoloPidor::hasScore).toList();
            for (int i = 0; i < foloPidors.size(); i++) {
                String place = switch (i) {
                    case 0 -> "\uD83E\uDD47";
                    case 1 -> "\uD83E\uDD48";
                    case 2 -> "\uD83E\uDD49";
                    default -> "\u2004*" + (i + 1) + "*.\u2004";
                };
                FoloPidor foloPidor = foloPidors.get(i);
                top.add(place + getFoloUserName(foloPidor) + " — _" +
                        Utils.getNumText(foloPidor.getScore(), NumTypeEnum.COUNT) + "_");
            }
            return buildMessage(top.toString(), update);
        } else {
            return buildMessage("Андрей - почетный фолопидор на все времена!", update);
        }
    }

    /**
     * Ответ на личное сообщение
     *
     * @param update {@link Update}
     * @return {@link BotApiMethod}
     */
    private BotApiMethod<?> onUserMessage(Update update) {
        if (isAndrew(update.getMessage().getFrom()) &&
                new SplittableRandom().nextInt(100) < 20) {
            forwardMessage(ChatId.getPOC_ID(), sendMessage(Text.getQuoteforAndrew(), update, true));
        }
        return null;
    }

    /**
     * Ответ на обращение
     *
     * @param update {@link Update}
     * @return {@link BotApiMethod}
     */
    private BotApiMethod<?> onReply(Update update) {
        // Cообщение в чат
        String text = update.getMessage().getText().toLowerCase();
        if (text.contains("привет") || new SplittableRandom().nextInt(100) < 20) {
            String userName = getFoloUserName(update.getMessage().getFrom());
            sendChatTyping(update);
            if (isAndrew(update.getMessage().getFrom())) {
                return buildMessage("Привет, моя сладкая бориспольская булочка!", update, true);
            } else {
                return buildMessage("Привет, уважаемый фолофил " + userName + "!", update, true);
            }
        }
        return null;
    }

    /**
     * Пользователь зашел в чат
     *
     * @param update {@link Update}
     * @return {@link BotApiMethod}
     */
    private BotApiMethod<?> onUserNew(Update update) {
        sendChatTyping(update);
        User user = update.getMessage().getNewChatMembers().get(0);
        if (isAndrew(user)) {
            return buildMessage("Наконец то ты вернулся, мой сладкий пирожочек Андрюша!", update, true);
        } else if (isVitalik(user)) {
            sendMessage("Как же я горю сейчас", update);
            sendMessage("Слово мужчини", update);
        } else if (isSelf(user)) {
            sendMessage("Привет, с вами я, сильный и незаурядный репер МС Фоломкин.", update);
            sendMessage("Спасибо, что вы смотрите мои замечательные видеоклипы.", update);
            sendMessage("Я читаю текст, вы слушаете текст", update);
        } else {
            if (isFolochat(update.getMessage().getChat())) {
                return buildMessage("Добро пожаловать в замечательный высокоинтеллектуальный фолочат, "
                        + getFoloUserName(user) + "!", update, true);
            } else {
                sendMessage("Это не настоящий фолочат, " + getFoloUserName(user) + "!", update);
                sendMessage("настоящий тут: \nt.me/alexfolomkin", update);
            }
        }
        return null;
    }

    /**
     * Пользователь покинул чат
     *
     * @param update {@link Update}
     * @return {@link BotApiMethod}
     */
    private BotApiMethod<?> onUserLeft(Update update) {
        sendChatTyping(update);
        User user = update.getMessage().getLeftChatMember();
        if (isAndrew(user)) {
            return buildMessage("Сладкая бориспольская булочка покинула чат", update);
        } else {
            return buildMessage("Куда же ты, " + getFoloUserName(user) + "! Не уходи!", update);
        }
    }

    /**
     * Пересылка личных сообщений
     *
     * @param update {@link Update}
     */
    private void forwardPrivate(Update update) {
        if (update.hasMessage())
            if (update.getMessage().isUserMessage()) {
                forwardMessage(ChatId.getPOC_ID(), update);
            } else if (isAndrew(update.getMessage().getFrom())) {
                forwardMessage(ChatId.getANDREWSLEGACY_ID(), update);
            }
    }

    private String getUserName(User user) {
        if (user != null) {
            return Stream.of(Stream.of(user.getFirstName(), user.getLastName())
                                    .filter(Objects::nonNull)
                                    .collect(Collectors.joining(" ")),
                            user.getUserName())
                    .filter(Objects::nonNull)
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }

    /**
     * Получение имени пользователя
     *
     * @param user {@link User}
     * @return Имя пользователя
     */
    private String getFoloUserName(User user) {
        FoloUser foloUser = foloUserRepo.findById(user.getId()).orElse(new FoloUser());
        // По тэгу
        String userName = foloUser.getTag();
        if (userName.isEmpty()) userName = getUserName(user);
        // По сохраненному имени
        if (userName == null || userName.isEmpty()) userName = foloUser.getName();
        // Если не удалось определить
        if (userName.isEmpty()) userName = "Загадочный незнакомец";
        return userName;
    }

    /**
     * Получение имени фолопидора
     *
     * @param foloPidor {@link FoloPidor}
     * @return Имя фолопидора
     */
    private String getFoloUserName(FoloPidor foloPidor) {
        // По тэгу
        String userName = foloPidor.getTag();
        // По пользователю
        if (userName.isEmpty()) userName = getUserName(getUserById(foloPidor.getId().getUserId()));
        // По сохраненному имени
        if (userName == null || userName.isEmpty()) userName = foloPidor.getName();
        // Если не удалось определить
        if (userName.isEmpty()) userName = "Загадочный незнакомец";
        return userName;
    }

    private User getUserById(Long userid) {
        try {
            return execute(new GetChatMember(Long.toString(userid), userid)).getUser();
        } catch (TelegramApiException ignored) {
            return null;
        }
    }

    /**
     * Получение кликабельного имени пользователя
     *
     * @param user {@link User}
     * @return Имя пользователя
     */
    private String getUserNameLinked(User user) {
        return "[" + getFoloUserName(user) + "](tg://user?id=" + user.getId() + ")";
    }

    /**
     * Получение кликабельного имени фолопидора
     *
     * @param foloPidor {@link FoloPidor}
     * @return Имя фолопидора
     */
    private String getUserNameLinked(FoloPidor foloPidor) {
        return "[" + getFoloUserName(foloPidor) + "](tg://user?id=" + foloPidor.getId().getUserId() + ")";
    }

    /**
     * Проверка что {@link User} это этот бот
     *
     * @param user {@link User}
     * @return да/нет
     */
    private boolean isSelf(User user) {
        try {
            return user != null && user.getId().equals(getMe().getId());
        } catch (TelegramApiException e) {
            return false;
        }
    }

    /**
     * Собрать объект {@link SendMessage}
     *
     * @param text   Текст сообщения
     * @param update {@link Update}
     * @return {@link SendMessage}
     */
    private SendMessage buildMessage(String text, Update update) {
        return SendMessage
                .builder()
                .parseMode(ParseMode.MARKDOWN)
                .chatId(Long.toString(update.getMessage().getChatId()))
                .text(text)
                .build();
    }

    /**
     * Собрать объект {@link SendMessage}
     *
     * @param text   Текст сообщения
     * @param update {@link Update}
     * @param reply  В ответ на сообщение
     * @return {@link SendMessage}
     */
    private SendMessage buildMessage(String text, Update update, boolean reply) {
        SendMessage sendMessage = buildMessage(text, update);
        if (reply) sendMessage.setReplyToMessageId(update.getMessage().getMessageId());
        return sendMessage;
    }

    /**
     * Отправить сообщение
     *
     * @param text   текст сообщения
     * @param chatid ID чата(пользователя)
     */
    public void sendMessage(String text, Long chatid) {
        try {
            execute(SendMessage
                    .builder()
                    .parseMode(ParseMode.MARKDOWN)
                    .chatId(Long.toString(chatid))
                    .text(text)
                    .build());
        } catch (TelegramApiException e) {
            printExeptionMessage(e);
        }
    }

    /**
     * Отправить сообщение
     *
     * @param text   текст сообщения
     * @param update {@link Update}
     * @return {@link Message}
     */
    public Message sendMessage(String text, Update update) {
        try {
            return execute(buildMessage(text, update));
        } catch (TelegramApiException e) {
            printExeptionMessage(e);
        }
        return null;
    }

    /**
     * Отправить сообщение буз реплая
     *
     * @param text   текст сообщения
     * @param update {@link Update}
     * @param reply  да/нет
     * @return {@link Message}
     */
    private Message sendMessage(String text, Update update, boolean reply) {
        if (!reply) {
            return this.sendMessage(text, update);
        } else {
            try {
                return execute(buildMessage(text, update, reply));
            } catch (TelegramApiException e) {
                printExeptionMessage(e);
            }
        }
        return null;
    }

    //TODO javadoc + возвращаться должно BotApiMethod
    private SendSticker buildSticker(InputFile stickerFile, Update update) {
        if (stickerFile != null) {
            return SendSticker
                    .builder()
                    .chatId(Long.toString(update.getMessage().getChatId()))
                    .sticker(stickerFile)
                    .replyToMessageId(update.getMessage().getMessageId())
                    .build();
        }
        return null;
    }

    /**
     * Отправить стикер
     *
     * @param stickerFile {@link InputFile}
     * @param update      {@link Update}
     */
    private void sendSticker(InputFile stickerFile, Update update) {
        if (stickerFile != null) {
            try {
                execute(buildSticker(stickerFile, update));
            } catch (TelegramApiException e) {
                printExeptionMessage(e);
            }
        }
    }

    /**
     * Пересылка сообщений
     *
     * @param chatid Чат куда будет переслано сообщение
     * @param update {@link Update}
     */
    private void forwardMessage(Long chatid, Update update) {
        try {
            execute(ForwardMessage
                    .builder()
                    .chatId(Long.toString(chatid))
                    .messageId(update.getMessage().getMessageId())
                    .fromChatId(Long.toString(update.getMessage().getChatId()))
                    .build());
        } catch (TelegramApiException e) {
            printExeptionMessage(e);
        }
    }

    private void forwardMessage(Long chatid, Message message) {
        if (message != null) {
            try {
                execute(ForwardMessage
                        .builder()
                        .chatId(Long.toString(chatid))
                        .messageId(message.getMessageId())
                        .fromChatId(Long.toString(message.getChatId()))
                        .build());
            } catch (TelegramApiException e) {
                printExeptionMessage(e);
            }
        }
    }

    /**
     * Отправить статус "печатает"
     *
     * @param update {@link Update}
     */
    private void sendChatTyping(Update update) {
        try {
            execute(SendChatAction
                    .builder()
                    .chatId(Long.toString(update.getMessage().getChatId()))
                    .action(String.valueOf(ActionType.TYPING))
                    .build());
        } catch (TelegramApiException e) {
            printExeptionMessage(e);
        }
    }

    /**
     * Получить случайный стикер (из гиф пака)
     *
     * @return {@link InputFile}
     */
    private InputFile getRandomSticker() {
        String[] stickers = {
                "CAACAgIAAxkBAAICCGKCCI-Ff-uqMZ-y4e0YmQEAAXp_RQAClxQAAnmaGEtOsbVbM13tniQE",
                "CAACAgIAAxkBAAPpYn7LsjgOH0OSJFBGx6WoIIKr_vcAAmQZAAJgRSBL_cLL_Nrl4OskBA",
                "CAACAgIAAxkBAAICCWKCCLoO6Itf6HSKKGedTPzbyeioAAJQFAACey0pSznSfTz0daK-JAQ",
                "CAACAgIAAxkBAAICCmKCCN_lePGRwqFYK4cPGBD4k_lpAAJcGQACmGshS9K8iR0VSuDVJAQ",
        };
        return new InputFile(stickers[new SplittableRandom().nextInt(stickers.length)]);
    }
}
