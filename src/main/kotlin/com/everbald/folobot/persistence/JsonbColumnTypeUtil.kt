package com.everbald.folobot.persistence

import com.everbald.folobot.config.objectMapper
import com.fasterxml.jackson.core.type.TypeReference
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ColumnType
import org.jetbrains.exposed.sql.Expression
import org.jetbrains.exposed.sql.ExpressionAlias
import org.jetbrains.exposed.sql.Function
import org.jetbrains.exposed.sql.Query
import org.jetbrains.exposed.sql.QueryBuilder
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.JavaInstantColumnType
import org.jetbrains.exposed.sql.statements.api.PreparedStatementApi
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.postgresql.util.PGobject
import java.sql.Timestamp

inline fun <reified T : Any> Table.jsonb(name: String): Column<T> =
    registerColumn(name, JsonColumnType(object : TypeReference<T>() {}))

class JsonColumnType<out T : Any>(private val typeReference: TypeReference<T>) : ColumnType() {
    override fun sqlType() = "jsonb"

    override fun valueFromDB(value: Any): Any {
        if (value !is PGobject) return value

        return try {
            objectMapper.readValue(value.value, typeReference)
        } catch (e: Exception) {
            e.printStackTrace()
            throw RuntimeException("Can't parse JSON: $value")
        }
    }

    override fun notNullValueToDB(value: Any): Any = objectMapper.writeValueAsString(value)

    override fun nonNullValueToString(value: Any): String = "'${objectMapper.writeValueAsString(value)}'"

    override fun setParameter(stmt: PreparedStatementApi, index: Int, value: Any?) {
        val obj = PGobject()
        obj.type = sqlType()
        obj.value = value?.let { it as String }
        stmt[index] = obj
    }
}

typealias JsonbCollection = Collection<Map<String, Any?>>

class JsonbCollectionFunction(
    val query: Query,
    val name: String,
) : Function<JsonbCollection>(JsonColumnType(object : TypeReference<JsonbCollection>() {})) {
    override fun toQueryBuilder(queryBuilder: QueryBuilder): Unit =
        queryBuilder {
            append("(" + query.prepareSQLJsonAgg(QueryBuilder(false)) + ")")
        }
}

fun JsonbCollection.toResultRows(columns: Collection<Expression<*>>): Collection<ResultRow> {
    val columnsByName = columns.associateBy {
        when (it) {
            is JsonbCollectionFunction -> it.name
            is Column -> "${it.table.tableName}.${it.name}"
            else -> throw IllegalArgumentException()
        }
    }
    return map { it.toResultRow(columnsByName) }
}

private fun Map<String, Any?>.toResultRow(columns: Map<String, Expression<*>>): ResultRow {
    val data = filterKeys { columns.containsKey(it) }.mapKeys { columns[it.key]!! }.mapValues {
        when (val key = it.key) {
            is JsonbCollectionFunction -> it.value
            is Column -> when (key.columnType) {
                is JavaInstantColumnType -> (it.value as String?)?.let { v -> Timestamp.valueOf(v.replace('T', ' ')) }
                is JsonColumnType<*> -> PGobject().apply {
                    type = "jsonb"
                    value = objectMapper.writeValueAsString(it.value)
                }
                else -> it.value
            }
            else -> throw IllegalArgumentException()
        }
    }
    return ResultRow(data.keys.mapIndexed { i, c -> c to i }.toMap()).also { row ->
        data.forEach { (c, v) -> row[c] = v }
    }
}

fun Query.prepareSQLJsonAgg(builder: QueryBuilder): String {
    builder {
        append("SELECT coalesce(jsonb_agg(jsonb_build_object(")

        set.realFields.appendTo {
            when (it) {
                is JsonbCollectionFunction -> append("'${it.name}',")
                is Column -> append("'${it.table.tableName}.${it.name}',")
                else -> throw IllegalArgumentException()
            }
            append(it)
        }

        append(")")

        if (orderByExpressions.isNotEmpty()) {
            append(" ORDER BY ")
            orderByExpressions.appendTo {
                append((it.first as? ExpressionAlias<*>)?.alias ?: it.first, " ", it.second.name)
            }
        }

        append("), '[]'::jsonb)")

        append(" FROM ")
        set.source.describe(TransactionManager.current(), this)

        where?.let {
            append(" WHERE ")
            +it
        }
    }
    return builder.toString()
}

private fun QueryBuilder.append(vararg expr: Any): QueryBuilder = apply {
    for (item in expr) {
        when (item) {
            is Char -> append(item)
            is String -> append(item)
            is Expression<*> -> append(item)
            else -> throw IllegalArgumentException("Can't append $item as it has unknown type")
        }
    }
}
