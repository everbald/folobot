package com.everbald.folobot.service.hh.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * 
 * @param acceptIncompleteResumes разрешен ли отклик на вакансию неполным резюме
 * @param alternateUrl Ссылка на представление вакансии на сайте
 * @param applyAlternateUrl Ссылка на отклик на вакансию на сайте
 * @param area 
 * @param department 
 * @param employer 
 * @param hasTest Информация о наличии прикрепленного тестового задании к вакансии
 * @param id Идентификатор вакансии
 * @param name Название
 * @param professionalRoles список профессиональных ролей
 * @param publishedAt Дата и время публикации вакансии
 * @param relations Возвращает связи соискателя с вакансией. Значения из поля `vacancy_relation` в [справочнике полей](#tag/Obshie-spravochniki/operation/get-dictionaries).
 * @param responseLetterRequired Обязательно ли заполнять сообщение при отклике на вакансию
 * @param salary 
 * @param type 
 * @param url URL вакансии
 * @param snippet 
 * @param acceptTemporary указание, что вакансия доступна с временным трудоустройством
 * @param address 
 * @param advResponseUrl URL для регистрации нажатия кнопки отклика
 * @param archived Находится ли данная вакансия в архиве
 * @param contacts 
 * @param createdAt Дата и время публикации вакансии
 * @param insiderInterview 
 * @param metroStations 
 * @param premium Является ли данная вакансия премиум-вакансией
 * @param responseUrl URL отклика для прямых вакансий (`type.id=direct`)
 * @param schedule 
 * @param sortPointDistance Расстояние в метрах между центром сортировки (заданной параметрами `sort_point_lat`, `sort_point_lng`) и указанным в вакансии адресом. В случае, если в адресе указаны только станции метро, выдается расстояние между центром сортировки и средней геометрической точкой указанных станций.  Значение `sort_point_distance` выдается только в случае, если заданы параметры `sort_point_lat`, `sort_point_lng`, `order_by=distance`
 * @param workingDays список рабочих дней
 * @param workingTimeIntervals список с временными интервалами работы
 * @param workingTimeModes список режимов времени работы
 * @param counters 
 * @param employment 
 * @param experience 
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class VacanciesVacanciesItem(

    @get:JsonProperty("accept_incomplete_resumes", required = true) val acceptIncompleteResumes: Boolean,

    @get:JsonProperty("alternate_url", required = true) val alternateUrl: String,

    @get:JsonProperty("apply_alternate_url", required = true) val applyAlternateUrl: String,

    @get:JsonProperty("area", required = true) val area: IncludesArea,

    @get:JsonProperty("department", required = false) val department: VacanciesVacancyCommonFieldsDepartment?,

    @get:JsonProperty("employer", required = true) val employer: VacanciesEmployerPublic,

    @get:JsonProperty("has_test", required = true) val hasTest: Boolean,

    @get:JsonProperty("id", required = true) val id: String,

    @get:JsonProperty("name", required = true) val name: String,

    @get:JsonProperty("professional_roles", required = true) val professionalRoles: List<VacancyProfessionalRoleItemOutput>,

    @get:JsonProperty("published_at", required = true) val publishedAt: String,

    @get:JsonProperty("relations", required = true) val relations: List<VacancyRelationItem>,

    @get:JsonProperty("response_letter_required", required = true) val responseLetterRequired: Boolean,

    @get:JsonProperty("salary", required = false) val salary: VacancySalary?,

    @get:JsonProperty("type", required = true) val type: VacancyTypeOutput,

    @get:JsonProperty("url", required = true) val url: String,

    @get:JsonProperty("snippet", required = true) val snippet: VacancySnippet,

    @get:JsonProperty("accept_temporary") val acceptTemporary: Boolean? = null,

    @get:JsonProperty("address") val address: VacancyAddressRawOutput? = null,

    @get:JsonProperty("adv_response_url") val advResponseUrl: String? = null,

    @get:JsonProperty("archived") val archived: Boolean? = null,

    @get:JsonProperty("contacts") val contacts: VacancyContactsOutput? = null,

    @get:JsonProperty("created_at") val createdAt: String? = null,

    @get:JsonProperty("insider_interview") val insiderInterview: VacanciesStandardVacancyFieldsInsiderInterview? = null,

    @get:JsonProperty("metro_stations") val metroStations: IncludesMetroStation? = null,

    @get:JsonProperty("premium") val premium: Boolean? = null,

    @get:JsonProperty("response_url") val responseUrl: String? = null,

    @get:JsonProperty("schedule") val schedule: VacanciesStandardVacancyFieldsSchedule? = null,

    @get:JsonProperty("sort_point_distance") val sortPointDistance: java.math.BigDecimal? = null,

    @get:JsonProperty("working_days") val workingDays: List<VacancyWorkingDayItemOutput>? = null,

    @get:JsonProperty("working_time_intervals") val workingTimeIntervals: List<VacancyWorkingTimeIntervalItemOutput>? = null,

    @get:JsonProperty("working_time_modes") val workingTimeModes: List<VacancyWorkingTimeModeItemOutput>? = null,

    @get:JsonProperty("counters") val counters: VacancyCounters? = null,

    @get:JsonProperty("employment") val employment: VacancyEmploymentOutput? = null,

    @get:JsonProperty("experience") val experience: VacancyExperienceOutput? = null,

    @get:JsonProperty("show_logo_in_search") val showLogoInSearch: Boolean? = null,

    @get:JsonProperty("is_adv_vacancy") val isAdvVacancy: Boolean? = null
) {

}

