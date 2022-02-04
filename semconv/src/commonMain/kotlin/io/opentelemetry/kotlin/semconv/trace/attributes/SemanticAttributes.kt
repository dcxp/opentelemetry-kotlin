/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.semconv.trace.attributes

import io.opentelemetry.kotlin.api.common.AttributeKey
import io.opentelemetry.kotlin.api.common.AttributeKey.Companion.booleanKey
import io.opentelemetry.kotlin.api.common.AttributeKey.Companion.doubleKey
import io.opentelemetry.kotlin.api.common.AttributeKey.Companion.longKey
import io.opentelemetry.kotlin.api.common.AttributeKey.Companion.stringArrayKey
import io.opentelemetry.kotlin.api.common.AttributeKey.Companion.stringKey

// DO NOT EDIT, this is an Auto-generated file from
// buildscripts/semantic-convention/templates/SemanticAttributes.java.j2
object SemanticAttributes {
    /** The URL of the OpenTelemetry schema for these keys and values. */
    const val SCHEMA_URL = "https://opentelemetry.io/schemas/1.7.0"

    /**
     * The full invoked ARN as provided on the `Context` passed to the function
     * (`Lambda-Runtime-Invoked-Function-Arn` header on the `/runtime/invocation/next` applicable).
     *
     * Notes:
     *
     * * This may be different from `faas.id` if an alias is involved.
     */
    val AWS_LAMBDA_INVOKED_ARN: AttributeKey<String> = stringKey("aws.lambda.invoked_arn")

    /**
     * An identifier for the database management system (DBMS) product being used. See below for a
     * list of well-known identifiers.
     */
    val DB_SYSTEM: AttributeKey<String> = stringKey("db.system")

    /**
     * The connection string used to connect to the database. It is recommended to remove embedded
     * credentials.
     */
    val DB_CONNECTION_STRING: AttributeKey<String> = stringKey("db.connection_string")

    /** Username for accessing the database. */
    val DB_USER: AttributeKey<String> = stringKey("db.user")

    /**
     * The fully-qualified class name of the
     * [Java Database Connectivity
     * (JDBC)](https://docs.oracle.com/javase/8/docs/technotes/guides/jdbc/)
     * driver used to connect.
     */
    val DB_JDBC_DRIVER_CLASSNAME: AttributeKey<String> = stringKey("db.jdbc.driver_classname")

    /**
     * If no [tech-specific attribute](#call-level-attributes-for-specific-technologies) is defined,
     * this attribute is used to report the name of the database being accessed. For commands that
     * switch the database, this should be set to the target database (even if the command fails).
     *
     * Notes:
     *
     * * In some SQL databases, the database name to be used is called &quot;schema name&quot;.
     */
    val DB_NAME: AttributeKey<String> = stringKey("db.name")

    /**
     * The database statement being executed.
     *
     * Notes:
     *
     * * The value may be sanitized to exclude sensitive information.
     */
    val DB_STATEMENT: AttributeKey<String> = stringKey("db.statement")

    /**
     * The name of the operation being executed, e.g. the
     * [MongoDB command
     * name](https://docs.mongodb.com/manual/reference/command/#database-operations)
     * such as `findAndModify`, or the SQL keyword.
     *
     * Notes:
     *
     * * When setting this to an SQL keyword, it is not recommended to attempt any client-side
     * parsing of `db.statement` just to get this property, but it should be set if the operation
     * name is provided by the library being instrumented. If the SQL statement has an ambiguous
     * operation, or performs more than one operation, this value may be omitted.
     */
    val DB_OPERATION: AttributeKey<String> = stringKey("db.operation")

    /**
     * The Microsoft SQL Server
     * [instance
     * name](https://docs.microsoft.com/en-us/sql/connect/jdbc/building-the-connection-url?view=sql-server-ver15)
     * connecting to. This name is used to determine the port of a named instance.
     *
     * Notes:
     *
     * * If setting a `db.mssql.instance_name`, `net.peer.port` is no longer required (but still
     * recommended if non-standard).
     */
    val DB_MSSQL_INSTANCE_NAME: AttributeKey<String> = stringKey("db.mssql.instance_name")

    /**
     * The name of the keyspace being accessed. To be used instead of the generic `db.name`
     * attribute.
     */
    val DB_CASSANDRA_KEYSPACE: AttributeKey<String> = stringKey("db.cassandra.keyspace")

    /** The fetch size used for paging, i.e. how many rows will be returned at once. */
    val DB_CASSANDRA_PAGE_SIZE: AttributeKey<Long> = longKey("db.cassandra.page_size")

    /**
     * The consistency level of the query. Based on consistency values from
     * [CQL](https://docs.datastax.com/en/cassandra-oss/3.0/cassandra/dml/dmlConfigConsistency.html)
     * .
     */
    val DB_CASSANDRA_CONSISTENCY_LEVEL: AttributeKey<String> =
        stringKey("db.cassandra.consistency_level")

    /**
     * The name of the primary table that the operation is acting upon, including the schema name
     * (if applicable).
     *
     * Notes:
     *
     * * This mirrors the db.sql.table attribute but references cassandra rather than sql. It is not
     * recommended to attempt any client-side parsing of `db.statement` just to get this property,
     * but it should be set if it is provided by the library being instrumented. If the operation is
     * acting upon an anonymous table, or more than one table, this value MUST NOT be set.
     */
    val DB_CASSANDRA_TABLE: AttributeKey<String> = stringKey("db.cassandra.table")

    /** Whether or not the query is idempotent. */
    val DB_CASSANDRA_IDEMPOTENCE: AttributeKey<Boolean> = booleanKey("db.cassandra.idempotence")

    /**
     * The number of times a query was speculatively executed. Not set or `0` if the query was not
     * executed speculatively.
     */
    val DB_CASSANDRA_SPECULATIVE_EXECUTION_COUNT: AttributeKey<Long> =
        longKey("db.cassandra.speculative_execution_count")

    /** The ID of the coordinating node for a query. */
    val DB_CASSANDRA_COORDINATOR_ID: AttributeKey<String> = stringKey("db.cassandra.coordinator.id")

    /** The data center of the coordinating node for a query. */
    val DB_CASSANDRA_COORDINATOR_DC: AttributeKey<String> = stringKey("db.cassandra.coordinator.dc")

    /**
     * The [HBase namespace](https://hbase.apache.org/book.html#_namespace) being accessed. To be
     * used instead of the generic `db.name` attribute.
     */
    val DB_HBASE_NAMESPACE: AttributeKey<String> = stringKey("db.hbase.namespace")

    /**
     * The index of the database being accessed as used in the
     * [`SELECT` command](https://redis.io/commands/select), provided as an integer. To be used
     * instead of the generic `db.name` attribute.
     */
    val DB_REDIS_DATABASE_INDEX: AttributeKey<Long> = longKey("db.redis.database_index")

    /** The collection being accessed within the database stated in `db.name`. */
    val DB_MONGODB_COLLECTION: AttributeKey<String> = stringKey("db.mongodb.collection")

    /**
     * The name of the primary table that the operation is acting upon, including the schema name
     * (if applicable).
     *
     * Notes:
     *
     * * It is not recommended to attempt any client-side parsing of `db.statement` just to get this
     * property, but it should be set if it is provided by the library being instrumented. If the
     * operation is acting upon an anonymous table, or more than one table, this value MUST NOT be
     * set.
     */
    val DB_SQL_TABLE: AttributeKey<String> = stringKey("db.sql.table")

    /**
     * The type of the exception (its fully-qualified class name, if applicable). The dynamic type
     * of the exception should be preferred over the static type in languages that support it.
     */
    val EXCEPTION_TYPE: AttributeKey<String> = stringKey("exception.type")

    /** The exception message. */
    val EXCEPTION_MESSAGE: AttributeKey<String> = stringKey("exception.message")

    /**
     * A stacktrace as a string in the natural representation for the language runtime. The
     * representation is to be determined and documented by each language SIG.
     */
    val EXCEPTION_STACKTRACE: AttributeKey<String> = stringKey("exception.stacktrace")

    /**
     * SHOULD be set to true if the exception event is recorded at a point where it is known that
     * the exception is escaping the scope of the span.
     *
     * Notes:
     *
     * * An exception is considered to have escaped (or left) the scope of a span, if that span is
     * ended while the exception is still logically &quot;in flight&quot;. This may be actually
     * &quot;in flight&quot; in some languages (e.g. if the exception is passed to a Context
     * manager's `__exit__` method in Python) but will usually be caught at the point of recording
     * the exception in most languages.
     * * It is usually not possible to determine at the point where an exception is thrown whether
     * it will escape the scope of a span. However, it is trivial to know that an exception will
     * escape, if one checks for an active exception just before ending the span, as done in the
     * [example above](#exception-end-example).
     * * It follows that an exception may still escape the scope of the span even if the
     * `exception.escaped` attribute was not set or set to false, since the event might have been
     * recorded at a time where it was not clear whether the exception will escape.
     */
    val EXCEPTION_ESCAPED: AttributeKey<Boolean> = booleanKey("exception.escaped")

    /** Type of the trigger on which the function is executed. */
    val FAAS_TRIGGER: AttributeKey<String> = stringKey("faas.trigger")

    /** The execution ID of the current function execution. */
    val FAAS_EXECUTION: AttributeKey<String> = stringKey("faas.execution")

    /**
     * The name of the source on which the triggering operation was performed. For example, in Cloud
     * Storage or S3 corresponds to the bucket name, and in Cosmos DB to the database name.
     */
    val FAAS_DOCUMENT_COLLECTION: AttributeKey<String> = stringKey("faas.document.collection")

    /** Describes the type of the operation that was performed on the data. */
    val FAAS_DOCUMENT_OPERATION: AttributeKey<String> = stringKey("faas.document.operation")

    /**
     * A string containing the time when the data was accessed in the
     * [ISO 8601](https://www.iso.org/iso-8601-date-and-time-format.html) format expressed in
     * [UTC](https://www.w3.org/TR/NOTE-datetime).
     */
    val FAAS_DOCUMENT_TIME: AttributeKey<String> = stringKey("faas.document.time")

    /**
     * The document name/table subjected to the operation. For example, in Cloud Storage or S3 is
     * the name of the file, and in Cosmos DB the table name.
     */
    val FAAS_DOCUMENT_NAME: AttributeKey<String> = stringKey("faas.document.name")

    /**
     * A string containing the function invocation time in the
     * [ISO 8601](https://www.iso.org/iso-8601-date-and-time-format.html) format expressed in
     * [UTC](https://www.w3.org/TR/NOTE-datetime).
     */
    val FAAS_TIME: AttributeKey<String> = stringKey("faas.time")

    /**
     * A string containing the schedule period as
     * [Cron
     * Expression](https://docs.oracle.com/cd/E12058_01/doc/doc.1014/e12030/cron_expressions.htm)
     * .
     */
    val FAAS_CRON: AttributeKey<String> = stringKey("faas.cron")

    /**
     * A boolean that is true if the serverless function is executed for the first time (aka
     * cold-start).
     */
    val FAAS_COLDSTART: AttributeKey<Boolean> = booleanKey("faas.coldstart")

    /**
     * The name of the invoked function.
     *
     * Notes:
     *
     * * SHOULD be equal to the `faas.name` resource attribute of the invoked function.
     */
    val FAAS_INVOKED_NAME: AttributeKey<String> = stringKey("faas.invoked_name")

    /**
     * The cloud provider of the invoked function.
     *
     * Notes:
     *
     * * SHOULD be equal to the `cloud.provider` resource attribute of the invoked function.
     */
    val FAAS_INVOKED_PROVIDER: AttributeKey<String> = stringKey("faas.invoked_provider")

    /**
     * The cloud region of the invoked function.
     *
     * Notes:
     *
     * * SHOULD be equal to the `cloud.region` resource attribute of the invoked function.
     */
    val FAAS_INVOKED_REGION: AttributeKey<String> = stringKey("faas.invoked_region")

    /** Transport protocol used. See note below. */
    val NET_TRANSPORT: AttributeKey<String> = stringKey("net.transport")

    /**
     * Remote address of the peer (dotted decimal for IPv4 or
     * [RFC5952](https://tools.ietf.org/html/rfc5952) for IPv6)
     */
    val NET_PEER_IP: AttributeKey<String> = stringKey("net.peer.ip")

    /** Remote port number. */
    val NET_PEER_PORT: AttributeKey<Long> = longKey("net.peer.port")

    /** Remote hostname or similar, see note below. */
    val NET_PEER_NAME: AttributeKey<String> = stringKey("net.peer.name")

    /** Like `net.peer.ip` but for the host IP. Useful in case of a multi-IP host. */
    val NET_HOST_IP: AttributeKey<String> = stringKey("net.host.ip")

    /** Like `net.peer.port` but for the host port. */
    val NET_HOST_PORT: AttributeKey<Long> = longKey("net.host.port")

    /** Local hostname or similar, see note below. */
    val NET_HOST_NAME: AttributeKey<String> = stringKey("net.host.name")

    /** The internet connection type currently being used by the host. */
    val NET_HOST_CONNECTION_TYPE: AttributeKey<String> = stringKey("net.host.connection.type")

    /**
     * This describes more details regarding the connection.type. It may be the type of cell
     * technology connection, but it could be used for describing details about a wifi connection.
     */
    val NET_HOST_CONNECTION_SUBTYPE: AttributeKey<String> = stringKey("net.host.connection.subtype")

    /** The name of the mobile carrier. */
    val NET_HOST_CARRIER_NAME: AttributeKey<String> = stringKey("net.host.carrier.name")

    /** The mobile carrier country code. */
    val NET_HOST_CARRIER_MCC: AttributeKey<String> = stringKey("net.host.carrier.mcc")

    /** The mobile carrier network code. */
    val NET_HOST_CARRIER_MNC: AttributeKey<String> = stringKey("net.host.carrier.mnc")

    /**
     * The ISO 3166-1 alpha-2 2-character country code associated with the mobile carrier network.
     */
    val NET_HOST_CARRIER_ICC: AttributeKey<String> = stringKey("net.host.carrier.icc")

    /**
     * The [`service.name`](../../resource/semantic_conventions/README.md#service) of the remote
     * service. SHOULD be equal to the actual `service.name` resource attribute of the remote
     * service if any.
     */
    val PEER_SERVICE: AttributeKey<String> = stringKey("peer.service")

    /**
     * Username or client_id extracted from the access token or
     * [Authorization](https://tools.ietf.org/html/rfc7235#section-4.2) header in the inbound
     * request from outside the system.
     */
    val ENDUSER_ID: AttributeKey<String> = stringKey("enduser.id")

    /**
     * Actual/assumed role the client is making the request under extracted from token or
     * application security context.
     */
    val ENDUSER_ROLE: AttributeKey<String> = stringKey("enduser.role")

    /**
     * Scopes or granted authorities the client currently possesses extracted from token or
     * application security context. The value would come from the scope associated with an
     * [OAuth 2.0 Access Token](https://tools.ietf.org/html/rfc6749#section-3.3) or an attribute
     * value in a
     * [SAML
     * 2.0 Assertion](http://docs.oasis-open.org/security/saml/Post2.0/sstc-saml-tech-overview-2.0.html)
     * .
     */
    val ENDUSER_SCOPE: AttributeKey<String> = stringKey("enduser.scope")

    /** Current &quot;managed&quot; thread ID (as opposed to OS thread ID). */
    val THREAD_ID: AttributeKey<Long> = longKey("thread.id")

    /** Current thread name. */
    val THREAD_NAME: AttributeKey<String> = stringKey("thread.name")

    /**
     * The method or function name, or equivalent (usually rightmost part of the code unit's name).
     */
    val CODE_FUNCTION: AttributeKey<String> = stringKey("code.function")

    /**
     * The &quot;namespace&quot; within which `code.function` is defined. Usually the qualified
     * class or module name, such that `code.namespace` + some separator + `code.function` form a
     * unique identifier for the code unit.
     */
    val CODE_NAMESPACE: AttributeKey<String> = stringKey("code.namespace")

    /**
     * The source code file name that identifies the code unit as uniquely as possible (preferably
     * an absolute file path).
     */
    val CODE_FILEPATH: AttributeKey<String> = stringKey("code.filepath")

    /**
     * The line number in `code.filepath` best representing the operation. It SHOULD point within
     * the code unit named in `code.function`.
     */
    val CODE_LINENO: AttributeKey<Long> = longKey("code.lineno")

    /** HTTP request method. */
    val HTTP_METHOD: AttributeKey<String> = stringKey("http.method")

    /**
     * Full HTTP request URL in the form `scheme://host[:port]/path?query[#fragment]`. Usually the
     * fragment is not transmitted over HTTP, but if it is known, it should be included
     * nevertheless.
     *
     * Notes:
     *
     * * `http.url` MUST NOT contain credentials passed via URL in form of
     * `https://username:password@www.example.com/`. In such case the attribute's value should be
     * `https://www.example.com/`.
     */
    val HTTP_URL: AttributeKey<String> = stringKey("http.url")

    /** The full request target as passed in a HTTP request line or equivalent. */
    val HTTP_TARGET: AttributeKey<String> = stringKey("http.target")

    /**
     * The value of the [HTTP host
     * header](https://tools.ietf.org/html/rfc7230#section-5.4).
     * An empty Host header should also be reported, see note.
     *
     * Notes:
     *
     * * When the header is present but empty the attribute SHOULD be set to the empty string. Note
     * that this is a valid situation that is expected in certain cases, according the
     * aforementioned [section of RFC
     * 7230](https://tools.ietf.org/html/rfc7230#section-5.4).
     * When the header is not set the attribute MUST NOT be set.
     */
    val HTTP_HOST: AttributeKey<String> = stringKey("http.host")

    /** The URI scheme identifying the used protocol. */
    val HTTP_SCHEME: AttributeKey<String> = stringKey("http.scheme")

    /** [HTTP response status code](https://tools.ietf.org/html/rfc7231#section-6). */
    val HTTP_STATUS_CODE: AttributeKey<Long> = longKey("http.status_code")

    /**
     * Kind of HTTP protocol used.
     *
     * Notes:
     *
     * * If `net.transport` is not specified, it can be assumed to be `IP.TCP` except if
     * `http.flavor` is `QUIC`, in which case `IP.UDP` is assumed.
     */
    val HTTP_FLAVOR: AttributeKey<String> = stringKey("http.flavor")

    /**
     * Value of the [HTTP User-Agent](https://tools.ietf.org/html/rfc7231#section-5.5.3) header sent
     * by the client.
     */
    val HTTP_USER_AGENT: AttributeKey<String> = stringKey("http.user_agent")

    /**
     * The size of the request payload body in bytes. This is the number of bytes transferred
     * excluding headers and is often, but not always, present as the
     * [Content-Length](https://tools.ietf.org/html/rfc7230#section-3.3.2) header. For requests
     * using transport encoding, this should be the compressed size.
     */
    val HTTP_REQUEST_CONTENT_LENGTH: AttributeKey<Long> = longKey("http.request_content_length")

    /**
     * The size of the uncompressed request payload body after transport decoding. Not set if
     * transport encoding not used.
     */
    val HTTP_REQUEST_CONTENT_LENGTH_UNCOMPRESSED: AttributeKey<Long> =
        longKey("http.request_content_length_uncompressed")

    /**
     * The size of the response payload body in bytes. This is the number of bytes transferred
     * excluding headers and is often, but not always, present as the
     * [Content-Length](https://tools.ietf.org/html/rfc7230#section-3.3.2) header. For requests
     * using transport encoding, this should be the compressed size.
     */
    val HTTP_RESPONSE_CONTENT_LENGTH: AttributeKey<Long> = longKey("http.response_content_length")

    /**
     * The size of the uncompressed response payload body after transport decoding. Not set if
     * transport encoding not used.
     */
    val HTTP_RESPONSE_CONTENT_LENGTH_UNCOMPRESSED: AttributeKey<Long> =
        longKey("http.response_content_length_uncompressed")

    /**
     * The primary server name of the matched virtual host. This should be obtained via
     * configuration. If no such configuration can be obtained, this attribute MUST NOT be set (
     * `net.host.name` should be used instead).
     *
     * Notes:
     *
     * * `http.url` is usually not readily available on the server side but would have to be
     * assembled in a cumbersome and sometimes lossy process from other information (see e.g.
     * open-telemetry/opentelemetry-python/pull/148). It is thus preferred to supply the raw data
     * that is available.
     */
    val HTTP_SERVER_NAME: AttributeKey<String> = stringKey("http.server_name")

    /** The matched route (path template). */
    val HTTP_ROUTE: AttributeKey<String> = stringKey("http.route")

    /**
     * The IP address of the original client behind all proxies, if known (e.g. from
     * [X-Forwarded-For](https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/X-Forwarded-For)
     * ).
     *
     * Notes:
     *
     * * This is not necessarily the same as `net.peer.ip`, which would identify the network-level
     * peer, which may be a proxy.
     * * This attribute should be set when a source of information different from the one used for
     * `net.peer.ip`, is available even if that other source just confirms the same value as
     * `net.peer.ip`. Rationale: For `net.peer.ip`, one typically does not know if it comes from a
     * proxy, reverse proxy, or the actual client. Setting `http.client_ip` when it's the same as
     * `net.peer.ip` means that one is at least somewhat confident that the address is not that of
     * the closest proxy.
     */
    val HTTP_CLIENT_IP: AttributeKey<String> = stringKey("http.client_ip")

    /** The keys in the `RequestItems` object field. */
    val AWS_DYNAMODB_TABLE_NAMES: AttributeKey<List<String>> =
        stringArrayKey("aws.dynamodb.table_names")

    /** The JSON-serialized value of each item in the `ConsumedCapacity` response field. */
    val AWS_DYNAMODB_CONSUMED_CAPACITY: AttributeKey<List<String>> =
        stringArrayKey("aws.dynamodb.consumed_capacity")

    /** The JSON-serialized value of the `ItemCollectionMetrics` response field. */
    val AWS_DYNAMODB_ITEM_COLLECTION_METRICS: AttributeKey<String> =
        stringKey("aws.dynamodb.item_collection_metrics")

    /** The value of the `ProvisionedThroughput.ReadCapacityUnits` request parameter. */
    val AWS_DYNAMODB_PROVISIONED_READ_CAPACITY: AttributeKey<Double> =
        doubleKey("aws.dynamodb.provisioned_read_capacity")

    /** The value of the `ProvisionedThroughput.WriteCapacityUnits` request parameter. */
    val AWS_DYNAMODB_PROVISIONED_WRITE_CAPACITY: AttributeKey<Double> =
        doubleKey("aws.dynamodb.provisioned_write_capacity")

    /** The value of the `ConsistentRead` request parameter. */
    val AWS_DYNAMODB_CONSISTENT_READ: AttributeKey<Boolean> =
        booleanKey("aws.dynamodb.consistent_read")

    /** The value of the `ProjectionExpression` request parameter. */
    val AWS_DYNAMODB_PROJECTION: AttributeKey<String> = stringKey("aws.dynamodb.projection")

    /** The value of the `Limit` request parameter. */
    val AWS_DYNAMODB_LIMIT: AttributeKey<Long> = longKey("aws.dynamodb.limit")

    /** The value of the `AttributesToGet` request parameter. */
    val AWS_DYNAMODB_ATTRIBUTES_TO_GET: AttributeKey<List<String>> =
        stringArrayKey("aws.dynamodb.attributes_to_get")

    /** The value of the `IndexName` request parameter. */
    val AWS_DYNAMODB_INDEX_NAME: AttributeKey<String> = stringKey("aws.dynamodb.index_name")

    /** The value of the `Select` request parameter. */
    val AWS_DYNAMODB_SELECT: AttributeKey<String> = stringKey("aws.dynamodb.select")

    /** The JSON-serialized value of each item of the `GlobalSecondaryIndexes` request field */
    val AWS_DYNAMODB_GLOBAL_SECONDARY_INDEXES: AttributeKey<List<String>> =
        stringArrayKey("aws.dynamodb.global_secondary_indexes")

    /** The JSON-serialized value of each item of the `LocalSecondaryIndexes` request field. */
    val AWS_DYNAMODB_LOCAL_SECONDARY_INDEXES: AttributeKey<List<String>> =
        stringArrayKey("aws.dynamodb.local_secondary_indexes")

    /** The value of the `ExclusiveStartTableName` request parameter. */
    val AWS_DYNAMODB_EXCLUSIVE_START_TABLE: AttributeKey<String> =
        stringKey("aws.dynamodb.exclusive_start_table")

    /** The the number of items in the `TableNames` response parameter. */
    val AWS_DYNAMODB_TABLE_COUNT: AttributeKey<Long> = longKey("aws.dynamodb.table_count")

    /** The value of the `ScanIndexForward` request parameter. */
    val AWS_DYNAMODB_SCAN_FORWARD: AttributeKey<Boolean> = booleanKey("aws.dynamodb.scan_forward")

    /** The value of the `Segment` request parameter. */
    val AWS_DYNAMODB_SEGMENT: AttributeKey<Long> = longKey("aws.dynamodb.segment")

    /** The value of the `TotalSegments` request parameter. */
    val AWS_DYNAMODB_TOTAL_SEGMENTS: AttributeKey<Long> = longKey("aws.dynamodb.total_segments")

    /** The value of the `Count` response parameter. */
    val AWS_DYNAMODB_COUNT: AttributeKey<Long> = longKey("aws.dynamodb.count")

    /** The value of the `ScannedCount` response parameter. */
    val AWS_DYNAMODB_SCANNED_COUNT: AttributeKey<Long> = longKey("aws.dynamodb.scanned_count")

    /** The JSON-serialized value of each item in the `AttributeDefinitions` request field. */
    val AWS_DYNAMODB_ATTRIBUTE_DEFINITIONS: AttributeKey<List<String>> =
        stringArrayKey("aws.dynamodb.attribute_definitions")

    /**
     * The JSON-serialized value of each item in the the `GlobalSecondaryIndexUpdates` request
     * field.
     */
    val AWS_DYNAMODB_GLOBAL_SECONDARY_INDEX_UPDATES: AttributeKey<List<String>> =
        stringArrayKey("aws.dynamodb.global_secondary_index_updates")

    /** A string identifying the messaging system. */
    val MESSAGING_SYSTEM: AttributeKey<String> = stringKey("messaging.system")

    /**
     * The message destination name. This might be equal to the span name but is required
     * nevertheless.
     */
    val MESSAGING_DESTINATION: AttributeKey<String> = stringKey("messaging.destination")

    /** The kind of message destination */
    val MESSAGING_DESTINATION_KIND: AttributeKey<String> = stringKey("messaging.destination_kind")

    /** A boolean that is true if the message destination is temporary. */
    val MESSAGING_TEMP_DESTINATION: AttributeKey<Boolean> = booleanKey("messaging.temp_destination")

    /** The name of the transport protocol. */
    val MESSAGING_PROTOCOL: AttributeKey<String> = stringKey("messaging.protocol")

    /** The version of the transport protocol. */
    val MESSAGING_PROTOCOL_VERSION: AttributeKey<String> = stringKey("messaging.protocol_version")

    /** Connection string. */
    val MESSAGING_URL: AttributeKey<String> = stringKey("messaging.url")

    /**
     * A value used by the messaging system as an identifier for the message, represented as a
     * string.
     */
    val MESSAGING_MESSAGE_ID: AttributeKey<String> = stringKey("messaging.message_id")

    /**
     * The [conversation ID](#conversations) identifying the conversation to which the message
     * belongs, represented as a string. Sometimes called &quot;Correlation ID&quot;.
     */
    val MESSAGING_CONVERSATION_ID: AttributeKey<String> = stringKey("messaging.conversation_id")

    /**
     * The (uncompressed) size of the message payload in bytes. Also use this attribute if it is
     * unknown whether the compressed or uncompressed payload size is reported.
     */
    val MESSAGING_MESSAGE_PAYLOAD_SIZE_BYTES: AttributeKey<Long> =
        longKey("messaging.message_payload_size_bytes")

    /** The compressed size of the message payload in bytes. */
    val MESSAGING_MESSAGE_PAYLOAD_COMPRESSED_SIZE_BYTES: AttributeKey<Long> =
        longKey("messaging.message_payload_compressed_size_bytes")

    /**
     * A string identifying the kind of message consumption as defined in the
     * [Operation names](#operation-names) section above. If the operation is &quot;send&quot;, this
     * attribute MUST NOT be set, since the operation can be inferred from the span kind in that
     * case.
     */
    val MESSAGING_OPERATION: AttributeKey<String> = stringKey("messaging.operation")

    /**
     * The identifier for the consumer receiving a message. For Kafka, set it to
     * `{messaging.kafka.consumer_group} - {messaging.kafka.client_id}`, if both are present, or
     * only `messaging.kafka.consumer_group`. For brokers, such as RabbitMQ and Artemis, set it to
     * the `client_id` of the client consuming the message.
     */
    val MESSAGING_CONSUMER_ID: AttributeKey<String> = stringKey("messaging.consumer_id")

    /** RabbitMQ message routing key. */
    val MESSAGING_RABBITMQ_ROUTING_KEY: AttributeKey<String> =
        stringKey("messaging.rabbitmq.routing_key")

    /**
     * Message keys in Kafka are used for grouping alike messages to ensure they're processed on the
     * same partition. They differ from `messaging.message_id` in that they're not unique. If the
     * key is `null`, the attribute MUST NOT be set.
     *
     * Notes:
     *
     * * If the key type is not string, it's string representation has to be supplied for the
     * attribute. If the key has no unambiguous, canonical string form, don't include its value.
     */
    val MESSAGING_KAFKA_MESSAGE_KEY: AttributeKey<String> = stringKey("messaging.kafka.message_key")

    /**
     * Name of the Kafka Consumer Group that is handling the message. Only applies to consumers, not
     * producers.
     */
    val MESSAGING_KAFKA_CONSUMER_GROUP: AttributeKey<String> =
        stringKey("messaging.kafka.consumer_group")

    /** Client Id for the Consumer or Producer that is handling the message. */
    val MESSAGING_KAFKA_CLIENT_ID: AttributeKey<String> = stringKey("messaging.kafka.client_id")

    /** Partition the message is sent to. */
    val MESSAGING_KAFKA_PARTITION: AttributeKey<Long> = longKey("messaging.kafka.partition")

    /** A boolean that is true if the message is a tombstone. */
    val MESSAGING_KAFKA_TOMBSTONE: AttributeKey<Boolean> = booleanKey("messaging.kafka.tombstone")

    /** A string identifying the remoting system. */
    val RPC_SYSTEM: AttributeKey<String> = stringKey("rpc.system")

    /**
     * The full (logical) name of the service being called, including its package name, if
     * applicable.
     *
     * Notes:
     *
     * * This is the logical name of the service from the RPC interface perspective, which can be
     * different from the name of any implementing class. The `code.namespace` attribute may be used
     * to store the latter (despite the attribute name, it may include a class name; e.g., class
     * with method actually executing the call on the server side, RPC client stub class on the
     * client side).
     */
    val RPC_SERVICE: AttributeKey<String> = stringKey("rpc.service")

    /**
     * The name of the (logical) method being called, must be equal to the $method part in the span
     * name.
     *
     * Notes:
     *
     * * This is the logical name of the method from the RPC interface perspective, which can be
     * different from the name of any implementing method/function. The `code.function` attribute
     * may be used to store the latter (e.g., method actually executing the call on the server side,
     * RPC client stub method on the client side).
     */
    val RPC_METHOD: AttributeKey<String> = stringKey("rpc.method")

    /**
     * The
     * [numeric status
     * code](https://github.com/grpc/grpc/blob/v1.33.2/doc/statuscodes.md) of
     * the gRPC request.
     */
    val RPC_GRPC_STATUS_CODE: AttributeKey<Long> = longKey("rpc.grpc.status_code")

    /**
     * Protocol version as in `jsonrpc` property of request/response. Since JSON-RPC 1.0 does not
     * specify this, the value can be omitted.
     */
    val RPC_JSONRPC_VERSION: AttributeKey<String> = stringKey("rpc.jsonrpc.version")

    /**
     * `id` property of request or response. Since protocol allows id to be int, string, `null` or
     * missing (for notifications), value is expected to be cast to string for simplicity. Use empty
     * string in case of `null` value. Omit entirely if this is a notification.
     */
    val RPC_JSONRPC_REQUEST_ID: AttributeKey<String> = stringKey("rpc.jsonrpc.request_id")

    /** `error.code` property of response if it is an error response. */
    val RPC_JSONRPC_ERROR_CODE: AttributeKey<Long> = longKey("rpc.jsonrpc.error_code")

    /** `error.message` property of response if it is an error response. */
    val RPC_JSONRPC_ERROR_MESSAGE: AttributeKey<String> = stringKey("rpc.jsonrpc.error_message")

    /** Whether this is a received or sent message. */
    val MESSAGE_TYPE: AttributeKey<String> = stringKey("message.type")

    /**
     * MUST be calculated as two different counters starting from `1` one for sent messages and one
     * for received message.
     *
     * Notes:
     *
     * * This way we guarantee that the values will be consistent between different implementations.
     */
    val MESSAGE_ID: AttributeKey<Long> = longKey("message.id")

    /** Compressed size of the message in bytes. */
    val MESSAGE_COMPRESSED_SIZE: AttributeKey<Long> = longKey("message.compressed_size")

    /** Uncompressed size of the message in bytes. */
    val MESSAGE_UNCOMPRESSED_SIZE: AttributeKey<Long> = longKey("message.uncompressed_size")
    // Manually defined and not YET in the YAML
    /**
     * The name of an event describing an exception.
     *
     * Typically an event with that name should not be manually created. Instead [ ]
     * [io.opentelemetry.kotlin.api.trace.Span.recordException] should be used.
     */
    const val EXCEPTION_EVENT_NAME = "exception"

    // Enum definitions
    object DbSystemValues {
        /** Some other SQL database. Fallback only. See notes. */
        const val OTHER_SQL = "other_sql"

        /** Microsoft SQL Server. */
        const val MSSQL = "mssql"

        /** MySQL. */
        const val MYSQL = "mysql"

        /** Oracle Database. */
        const val ORACLE = "oracle"

        /** IBM Db2. */
        const val DB2 = "db2"

        /** PostgreSQL. */
        const val POSTGRESQL = "postgresql"

        /** Amazon Redshift. */
        const val REDSHIFT = "redshift"

        /** Apache Hive. */
        const val HIVE = "hive"

        /** Cloudscape. */
        const val CLOUDSCAPE = "cloudscape"

        /** HyperSQL DataBase. */
        const val HSQLDB = "hsqldb"

        /** Progress Database. */
        const val PROGRESS = "progress"

        /** SAP MaxDB. */
        const val MAXDB = "maxdb"

        /** SAP HANA. */
        const val HANADB = "hanadb"

        /** Ingres. */
        const val INGRES = "ingres"

        /** FirstSQL. */
        const val FIRSTSQL = "firstsql"

        /** EnterpriseDB. */
        const val EDB = "edb"

        /** InterSystems Caché. */
        const val CACHE = "cache"

        /** Adabas (Adaptable Database System). */
        const val ADABAS = "adabas"

        /** Firebird. */
        const val FIREBIRD = "firebird"

        /** Apache Derby. */
        const val DERBY = "derby"

        /** FileMaker. */
        const val FILEMAKER = "filemaker"

        /** Informix. */
        const val INFORMIX = "informix"

        /** InstantDB. */
        const val INSTANTDB = "instantdb"

        /** InterBase. */
        const val INTERBASE = "interbase"

        /** MariaDB. */
        const val MARIADB = "mariadb"

        /** Netezza. */
        const val NETEZZA = "netezza"

        /** Pervasive PSQL. */
        const val PERVASIVE = "pervasive"

        /** PointBase. */
        const val POINTBASE = "pointbase"

        /** SQLite. */
        const val SQLITE = "sqlite"

        /** Sybase. */
        const val SYBASE = "sybase"

        /** Teradata. */
        const val TERADATA = "teradata"

        /** Vertica. */
        const val VERTICA = "vertica"

        /** H2. */
        const val H2 = "h2"

        /** ColdFusion IMQ. */
        const val COLDFUSION = "coldfusion"

        /** Apache Cassandra. */
        const val CASSANDRA = "cassandra"

        /** Apache HBase. */
        const val HBASE = "hbase"

        /** MongoDB. */
        const val MONGODB = "mongodb"

        /** Redis. */
        const val REDIS = "redis"

        /** Couchbase. */
        const val COUCHBASE = "couchbase"

        /** CouchDB. */
        const val COUCHDB = "couchdb"

        /** Microsoft Azure Cosmos DB. */
        const val COSMOSDB = "cosmosdb"

        /** Amazon DynamoDB. */
        const val DYNAMODB = "dynamodb"

        /** Neo4j. */
        const val NEO4J = "neo4j"

        /** Apache Geode. */
        const val GEODE = "geode"

        /** Elasticsearch. */
        const val ELASTICSEARCH = "elasticsearch"

        /** Memcached. */
        const val MEMCACHED = "memcached"

        /** CockroachDB. */
        const val COCKROACHDB = "cockroachdb"
    }

    object DbCassandraConsistencyLevelValues {
        /** all. */
        const val ALL = "all"

        /** each_quorum. */
        const val EACH_QUORUM = "each_quorum"

        /** quorum. */
        const val QUORUM = "quorum"

        /** local_quorum. */
        const val LOCAL_QUORUM = "local_quorum"

        /** one. */
        const val ONE = "one"

        /** two. */
        const val TWO = "two"

        /** three. */
        const val THREE = "three"

        /** local_one. */
        const val LOCAL_ONE = "local_one"

        /** any. */
        const val ANY = "any"

        /** serial. */
        const val SERIAL = "serial"

        /** local_serial. */
        const val LOCAL_SERIAL = "local_serial"
    }

    object FaasTriggerValues {
        /** A response to some data source operation such as a database or filesystem read/write. */
        const val DATASOURCE = "datasource"

        /** To provide an answer to an inbound HTTP request. */
        const val HTTP = "http"

        /** A function is set to be executed when messages are sent to a messaging system. */
        const val PUBSUB = "pubsub"

        /** A function is scheduled to be executed regularly. */
        const val TIMER = "timer"

        /** If none of the others apply. */
        const val OTHER = "other"
    }

    object FaasDocumentOperationValues {
        /** When a new object is created. */
        const val INSERT = "insert"

        /** When an object is modified. */
        const val EDIT = "edit"

        /** When an object is deleted. */
        const val DELETE = "delete"
    }

    object FaasInvokedProviderValues {
        /** Alibaba Cloud. */
        const val ALIBABA_CLOUD = "alibaba_cloud"

        /** Amazon Web Services. */
        const val AWS = "aws"

        /** Microsoft Azure. */
        const val AZURE = "azure"

        /** Google Cloud Platform. */
        const val GCP = "gcp"
    }

    object NetTransportValues {
        /** ip_tcp. */
        const val IP_TCP = "ip_tcp"

        /** ip_udp. */
        const val IP_UDP = "ip_udp"

        /** Another IP-based protocol. */
        const val IP = "ip"

        /** Unix Domain socket. See below. */
        const val UNIX = "unix"

        /** Named or anonymous pipe. See note below. */
        const val PIPE = "pipe"

        /** In-process communication. */
        const val INPROC = "inproc"

        /** Something else (non IP-based). */
        const val OTHER = "other"
    }

    object NetHostConnectionTypeValues {
        /** wifi. */
        const val WIFI = "wifi"

        /** wired. */
        const val WIRED = "wired"

        /** cell. */
        const val CELL = "cell"

        /** unavailable. */
        const val UNAVAILABLE = "unavailable"

        /** unknown. */
        const val UNKNOWN = "unknown"
    }

    object NetHostConnectionSubtypeValues {
        /** GPRS. */
        const val GPRS = "gprs"

        /** EDGE. */
        const val EDGE = "edge"

        /** UMTS. */
        const val UMTS = "umts"

        /** CDMA. */
        const val CDMA = "cdma"

        /** EVDO Rel. 0. */
        const val EVDO_0 = "evdo_0"

        /** EVDO Rev. A. */
        const val EVDO_A = "evdo_a"

        /** CDMA2000 1XRTT. */
        const val CDMA2000_1XRTT = "cdma2000_1xrtt"

        /** HSDPA. */
        const val HSDPA = "hsdpa"

        /** HSUPA. */
        const val HSUPA = "hsupa"

        /** HSPA. */
        const val HSPA = "hspa"

        /** IDEN. */
        const val IDEN = "iden"

        /** EVDO Rev. B. */
        const val EVDO_B = "evdo_b"

        /** LTE. */
        const val LTE = "lte"

        /** EHRPD. */
        const val EHRPD = "ehrpd"

        /** HSPAP. */
        const val HSPAP = "hspap"

        /** GSM. */
        const val GSM = "gsm"

        /** TD-SCDMA. */
        const val TD_SCDMA = "td_scdma"

        /** IWLAN. */
        const val IWLAN = "iwlan"

        /** 5G NR (New Radio). */
        const val NR = "nr"

        /** 5G NRNSA (New Radio Non-Standalone). */
        const val NRNSA = "nrnsa"

        /** LTE CA. */
        const val LTE_CA = "lte_ca"
    }

    object HttpFlavorValues {
        /** HTTP 1.0. */
        const val HTTP_1_0 = "1.0"

        /** HTTP 1.1. */
        const val HTTP_1_1 = "1.1"

        /** HTTP 2. */
        const val HTTP_2_0 = "2.0"

        /** SPDY protocol. */
        const val SPDY = "SPDY"

        /** QUIC protocol. */
        const val QUIC = "QUIC"
    }

    object MessagingDestinationKindValues {
        /** A message sent to a queue. */
        const val QUEUE = "queue"

        /** A message sent to a topic. */
        const val TOPIC = "topic"
    }

    object MessagingOperationValues {
        /** receive. */
        const val RECEIVE = "receive"

        /** process. */
        const val PROCESS = "process"
    }

    object RpcGrpcStatusCodeValues {
        /** OK. */
        const val OK: Long = 0

        /** CANCELLED. */
        const val CANCELLED: Long = 1

        /** UNKNOWN. */
        const val UNKNOWN: Long = 2

        /** INVALID_ARGUMENT. */
        const val INVALID_ARGUMENT: Long = 3

        /** DEADLINE_EXCEEDED. */
        const val DEADLINE_EXCEEDED: Long = 4

        /** NOT_FOUND. */
        const val NOT_FOUND: Long = 5

        /** ALREADY_EXISTS. */
        const val ALREADY_EXISTS: Long = 6

        /** PERMISSION_DENIED. */
        const val PERMISSION_DENIED: Long = 7

        /** RESOURCE_EXHAUSTED. */
        const val RESOURCE_EXHAUSTED: Long = 8

        /** FAILED_PRECONDITION. */
        const val FAILED_PRECONDITION: Long = 9

        /** ABORTED. */
        const val ABORTED: Long = 10

        /** OUT_OF_RANGE. */
        const val OUT_OF_RANGE: Long = 11

        /** UNIMPLEMENTED. */
        const val UNIMPLEMENTED: Long = 12

        /** INTERNAL. */
        const val INTERNAL: Long = 13

        /** UNAVAILABLE. */
        const val UNAVAILABLE: Long = 14

        /** DATA_LOSS. */
        const val DATA_LOSS: Long = 15

        /** UNAUTHENTICATED. */
        const val UNAUTHENTICATED: Long = 16
    }

    object MessageTypeValues {
        /** sent. */
        const val SENT = "SENT"

        /** received. */
        const val RECEIVED = "RECEIVED"
    }
}
