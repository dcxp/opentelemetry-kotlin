/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.semconv.resource.attributes

import io.opentelemetry.api.common.AttributeKey
import io.opentelemetry.api.common.AttributeKey.Companion.longKey
import io.opentelemetry.api.common.AttributeKey.Companion.stringArrayKey
import io.opentelemetry.api.common.AttributeKey.Companion.stringKey

// DO NOT EDIT, this is an Auto-generated file from
// buildscripts/semantic-convention/templates/SemanticAttributes.java.j2
object ResourceAttributes {
    /** The URL of the OpenTelemetry schema for these keys and values. */
    const val SCHEMA_URL = "https://opentelemetry.io/schemas/1.7.0"

    /** Name of the cloud provider. */
    val CLOUD_PROVIDER: AttributeKey<String> = stringKey("cloud.provider")

    /** The cloud account ID the resource is assigned to. */
    val CLOUD_ACCOUNT_ID: AttributeKey<String> = stringKey("cloud.account.id")

    /**
     * The geographical region the resource is running. Refer to your provider's docs to see the
     * available regions, for example
     * [Alibaba Cloud regions](https://www.alibabacloud.com/help/doc-detail/40654.htm),
     * [AWS regions](https://aws.amazon.com/about-aws/global-infrastructure/regions_az/),
     * [Azure regions](https://azure.microsoft.com/en-us/global-infrastructure/geographies/), or
     * [Google Cloud regions](https://cloud.google.com/about/locations).
     */
    val CLOUD_REGION: AttributeKey<String> = stringKey("cloud.region")

    /**
     * Cloud regions often have multiple, isolated locations known as zones to increase
     * availability. Availability zone represents the zone where the resource is running.
     *
     * Notes:
     *
     * * Availability zones are called &quot;zones&quot; on Alibaba Cloud and Google Cloud.
     */
    val CLOUD_AVAILABILITY_ZONE: AttributeKey<String> = stringKey("cloud.availability_zone")

    /**
     * The cloud platform in use.
     *
     * Notes:
     *
     * * The prefix of the service SHOULD match the one specified in `cloud.provider`.
     */
    val CLOUD_PLATFORM: AttributeKey<String> = stringKey("cloud.platform")

    /**
     * The Amazon Resource Name (ARN) of an
     * [ECS
     * container instance](https://docs.aws.amazon.com/AmazonECS/latest/developerguide/ECS_instances.html)
     * .
     */
    val AWS_ECS_CONTAINER_ARN: AttributeKey<String> = stringKey("aws.ecs.container.arn")

    /**
     * The ARN of an
     * [ECS
     * cluster](https://docs.aws.amazon.com/AmazonECS/latest/developerguide/clusters.html)
     * .
     */
    val AWS_ECS_CLUSTER_ARN: AttributeKey<String> = stringKey("aws.ecs.cluster.arn")

    /**
     * The
     * [launch
     * type](https://docs.aws.amazon.com/AmazonECS/latest/developerguide/launch_types.html)
     * for an ECS task.
     */
    val AWS_ECS_LAUNCHTYPE: AttributeKey<String> = stringKey("aws.ecs.launchtype")

    /**
     * The ARN of an
     * [ECS
     * task definition](https://docs.aws.amazon.com/AmazonECS/latest/developerguide/task_definitions.html)
     * .
     */
    val AWS_ECS_TASK_ARN: AttributeKey<String> = stringKey("aws.ecs.task.arn")

    /** The task definition family this task definition is a member of. */
    val AWS_ECS_TASK_FAMILY: AttributeKey<String> = stringKey("aws.ecs.task.family")

    /** The revision for this task definition. */
    val AWS_ECS_TASK_REVISION: AttributeKey<String> = stringKey("aws.ecs.task.revision")

    /** The ARN of an EKS cluster. */
    val AWS_EKS_CLUSTER_ARN: AttributeKey<String> = stringKey("aws.eks.cluster.arn")

    /**
     * The name(s) of the AWS log group(s) an application is writing to.
     *
     * Notes:
     *
     * * Multiple log groups must be supported for cases like multi-container applications, where a
     * single application has sidecar containers, and each write to their own log group.
     */
    val AWS_LOG_GROUP_NAMES: AttributeKey<List<String>> = stringArrayKey("aws.log.group.names")

    /**
     * The Amazon Resource Name(s) (ARN) of the AWS log group(s).
     *
     * Notes:
     *
     * * See the
     * [log
     * group ARN format documentation](https://docs.aws.amazon.com/AmazonCloudWatch/latest/logs/iam-access-control-overview-cwl.html#CWL_ARN_Format)
     * .
     */
    val AWS_LOG_GROUP_ARNS: AttributeKey<List<String>> = stringArrayKey("aws.log.group.arns")

    /** The name(s) of the AWS log stream(s) an application is writing to. */
    val AWS_LOG_STREAM_NAMES: AttributeKey<List<String>> = stringArrayKey("aws.log.stream.names")

    /**
     * The ARN(s) of the AWS log stream(s).
     *
     * Notes:
     *
     * * See the
     * [log
     * stream ARN format documentation](https://docs.aws.amazon.com/AmazonCloudWatch/latest/logs/iam-access-control-overview-cwl.html#CWL_ARN_Format)
     * . One log group can contain several log streams, so these ARNs necessarily identify both a
     * log group and a log stream.
     */
    val AWS_LOG_STREAM_ARNS: AttributeKey<List<String>> = stringArrayKey("aws.log.stream.arns")

    /** Container name. */
    val CONTAINER_NAME: AttributeKey<String> = stringKey("container.name")

    /**
     * Container ID. Usually a UUID, as for example used to
     * [identify Docker
     * containers](https://docs.docker.com/engine/reference/run/#container-identification)
     * . The UUID might be abbreviated.
     */
    val CONTAINER_ID: AttributeKey<String> = stringKey("container.id")

    /** The container runtime managing this container. */
    val CONTAINER_RUNTIME: AttributeKey<String> = stringKey("container.runtime")

    /** Name of the image the container was built on. */
    val CONTAINER_IMAGE_NAME: AttributeKey<String> = stringKey("container.image.name")

    /** Container image tag. */
    val CONTAINER_IMAGE_TAG: AttributeKey<String> = stringKey("container.image.tag")

    /**
     * Name of the
     * [deployment
     * environment](https://en.wikipedia.org/wiki/Deployment_environment) (aka
     * deployment tier).
     */
    val DEPLOYMENT_ENVIRONMENT: AttributeKey<String> = stringKey("deployment.environment")

    /**
     * A unique identifier representing the device
     *
     * Notes:
     *
     * * The device identifier MUST only be defined using the values outlined below. This value is
     * not an advertising identifier and MUST NOT be used as such. On iOS (Swift or Objective-C),
     * this value MUST be equal to the
     * [vendor
     * identifier](https://developer.apple.com/documentation/uikit/uidevice/1620059-identifierforvendor)
     * . On Android (Java or Kotlin), this value MUST be equal to the Firebase Installation ID or a
     * globally unique UUID which is persisted across sessions in your application. More information
     * can be found [here](https://developer.android.com/training/articles/user-data-ids) on best
     * practices and exact implementation details. Caution should be taken when storing personal
     * data or anything which can identify a user. GDPR and data protection laws may apply, ensure
     * you do your own due diligence.
     */
    val DEVICE_ID: AttributeKey<String> = stringKey("device.id")

    /**
     * The model identifier for the device
     *
     * Notes:
     *
     * * It's recommended this value represents a machine readable version of the model identifier
     * rather than the market or consumer-friendly name of the device.
     */
    val DEVICE_MODEL_IDENTIFIER: AttributeKey<String> = stringKey("device.model.identifier")

    /**
     * The marketing name for the device model
     *
     * Notes:
     *
     * * It's recommended this value represents a human readable version of the device model rather
     * than a machine readable alternative.
     */
    val DEVICE_MODEL_NAME: AttributeKey<String> = stringKey("device.model.name")

    /**
     * The name of the single function that this runtime instance executes.
     *
     * Notes:
     *
     * * This is the name of the function as configured/deployed on the FaaS platform and is usually
     * different from the name of the callback function (which may be stored in the
     * [`code.namespace`/`code.function`](../../trace/semantic_conventions/span-general.md#source-code-attributes)
     * span attributes).
     */
    val FAAS_NAME: AttributeKey<String> = stringKey("faas.name")

    /**
     * The unique ID of the single function that this runtime instance executes.
     *
     * Notes:
     *
     * * Depending on the cloud provider, use:
     * * **AWS Lambda:** The function
     * [ARN](https://docs.aws.amazon.com/general/latest/gr/aws-arns-and-namespaces.html).
     * * Take care not to use the &quot;invoked ARN&quot; directly but replace any
     * [alias
     * suffix](https://docs.aws.amazon.com/lambda/latest/dg/configuration-aliases.html)
     * with the resolved function version, as the same runtime instance may be invokable with
     * multiple different aliases.
     * * **GCP:** The [URI of the resource](https://cloud.google.com/iam/docs/full-resource-names)
     * * **Azure:** The
     * [Fully
     * Qualified Resource ID](https://docs.microsoft.com/en-us/rest/api/resources/resources/get-by-id)
     * .
     * * On some providers, it may not be possible to determine the full ID at startup, which is why
     * this field cannot be made required. For example, on AWS the account ID part of the ARN is not
     * available without calling another AWS API which may be deemed too slow for a short-running
     * lambda function. As an alternative, consider setting `faas.id` as a span attribute instead.
     */
    val FAAS_ID: AttributeKey<String> = stringKey("faas.id")

    /**
     * The immutable version of the function being executed.
     *
     * Notes:
     *
     * * Depending on the cloud provider and platform, use:
     * * **AWS Lambda:** The
     * [function
     * version](https://docs.aws.amazon.com/lambda/latest/dg/configuration-versions.html)
     * (an integer represented as a decimal string).
     * * **Google Cloud Run:** The [revision](https://cloud.google.com/run/docs/managing/revisions)
     * (i.e., the function name plus the revision suffix).
     * * **Google Cloud Functions:** The value of the
     * [`K_REVISION` environment variable](https://cloud.google.com/functions/docs/env-var#runtime_environment_variables_set_automatically)
     * .
     * * **Azure Functions:** Not applicable. Do not set this attribute.
     */
    val FAAS_VERSION: AttributeKey<String> = stringKey("faas.version")

    /**
     * The execution environment ID as a string, that will be potentially reused for other
     * invocations to the same function/function version.
     *
     * Notes:
     *
     * * **AWS Lambda:** Use the (full) log stream name.
     */
    val FAAS_INSTANCE: AttributeKey<String> = stringKey("faas.instance")

    /**
     * The amount of memory available to the serverless function in MiB.
     *
     * Notes:
     *
     * * It's recommended to set this attribute since e.g. too little memory can easily stop a Java
     * AWS Lambda function from working correctly. On AWS Lambda, the environment variable
     * `AWS_LAMBDA_FUNCTION_MEMORY_SIZE` provides this information.
     */
    val FAAS_MAX_MEMORY: AttributeKey<Long> = longKey("faas.max_memory")

    /** Unique host ID. For Cloud, this must be the instance_id assigned by the cloud provider. */
    val HOST_ID: AttributeKey<String> = stringKey("host.id")

    /**
     * Name of the host. On Unix systems, it may contain what the hostname command returns, or the
     * fully qualified hostname, or another name specified by the user.
     */
    val HOST_NAME: AttributeKey<String> = stringKey("host.name")

    /** Type of host. For Cloud, this must be the machine type. */
    val HOST_TYPE: AttributeKey<String> = stringKey("host.type")

    /** The CPU architecture the host system is running on. */
    val HOST_ARCH: AttributeKey<String> = stringKey("host.arch")

    /** Name of the VM image or OS install the host was instantiated from. */
    val HOST_IMAGE_NAME: AttributeKey<String> = stringKey("host.image.name")

    /** VM image ID. For Cloud, this value is from the provider. */
    val HOST_IMAGE_ID: AttributeKey<String> = stringKey("host.image.id")

    /**
     * The version string of the VM image as defined in
     * [Version
     * Attributes](README.md#version-attributes).
     */
    val HOST_IMAGE_VERSION: AttributeKey<String> = stringKey("host.image.version")

    /** The name of the cluster. */
    val K8S_CLUSTER_NAME: AttributeKey<String> = stringKey("k8s.cluster.name")

    /** The name of the Node. */
    val K8S_NODE_NAME: AttributeKey<String> = stringKey("k8s.node.name")

    /** The UID of the Node. */
    val K8S_NODE_UID: AttributeKey<String> = stringKey("k8s.node.uid")

    /** The name of the namespace that the pod is running in. */
    val K8S_NAMESPACE_NAME: AttributeKey<String> = stringKey("k8s.namespace.name")

    /** The UID of the Pod. */
    val K8S_POD_UID: AttributeKey<String> = stringKey("k8s.pod.uid")

    /** The name of the Pod. */
    val K8S_POD_NAME: AttributeKey<String> = stringKey("k8s.pod.name")

    /** The name of the Container in a Pod template. */
    val K8S_CONTAINER_NAME: AttributeKey<String> = stringKey("k8s.container.name")

    /** The UID of the ReplicaSet. */
    val K8S_REPLICASET_UID: AttributeKey<String> = stringKey("k8s.replicaset.uid")

    /** The name of the ReplicaSet. */
    val K8S_REPLICASET_NAME: AttributeKey<String> = stringKey("k8s.replicaset.name")

    /** The UID of the Deployment. */
    val K8S_DEPLOYMENT_UID: AttributeKey<String> = stringKey("k8s.deployment.uid")

    /** The name of the Deployment. */
    val K8S_DEPLOYMENT_NAME: AttributeKey<String> = stringKey("k8s.deployment.name")

    /** The UID of the StatefulSet. */
    val K8S_STATEFULSET_UID: AttributeKey<String> = stringKey("k8s.statefulset.uid")

    /** The name of the StatefulSet. */
    val K8S_STATEFULSET_NAME: AttributeKey<String> = stringKey("k8s.statefulset.name")

    /** The UID of the DaemonSet. */
    val K8S_DAEMONSET_UID: AttributeKey<String> = stringKey("k8s.daemonset.uid")

    /** The name of the DaemonSet. */
    val K8S_DAEMONSET_NAME: AttributeKey<String> = stringKey("k8s.daemonset.name")

    /** The UID of the Job. */
    val K8S_JOB_UID: AttributeKey<String> = stringKey("k8s.job.uid")

    /** The name of the Job. */
    val K8S_JOB_NAME: AttributeKey<String> = stringKey("k8s.job.name")

    /** The UID of the CronJob. */
    val K8S_CRONJOB_UID: AttributeKey<String> = stringKey("k8s.cronjob.uid")

    /** The name of the CronJob. */
    val K8S_CRONJOB_NAME: AttributeKey<String> = stringKey("k8s.cronjob.name")

    /** The operating system type. */
    val OS_TYPE: AttributeKey<String> = stringKey("os.type")

    /**
     * Human readable (not intended to be parsed) OS version information, like e.g. reported by
     * `ver` or `lsb_release -a` commands.
     */
    val OS_DESCRIPTION: AttributeKey<String> = stringKey("os.description")

    /** Human readable operating system name. */
    val OS_NAME: AttributeKey<String> = stringKey("os.name")

    /**
     * The version string of the operating system as defined in
     * [Version Attributes](../../resource/semantic_conventions/README.md#version-attributes).
     */
    val OS_VERSION: AttributeKey<String> = stringKey("os.version")

    /** Process identifier (PID). */
    val PROCESS_PID: AttributeKey<Long> = longKey("process.pid")

    /**
     * The name of the process executable. On Linux based systems, can be set to the `Name` in
     * `proc/[pid]/status`. On Windows, can be set to the base name of `GetProcessImageFileNameW`.
     */
    val PROCESS_EXECUTABLE_NAME: AttributeKey<String> = stringKey("process.executable.name")

    /**
     * The full path to the process executable. On Linux based systems, can be set to the target of
     * `proc/[pid]/exe`. On Windows, can be set to the result of `GetProcessImageFileNameW`.
     */
    val PROCESS_EXECUTABLE_PATH: AttributeKey<String> = stringKey("process.executable.path")

    /**
     * The command used to launch the process (i.e. the command name). On Linux based systems, can
     * be set to the zeroth string in `proc/[pid]/cmdline`. On Windows, can be set to the first
     * parameter extracted from `GetCommandLineW`.
     */
    val PROCESS_COMMAND: AttributeKey<String> = stringKey("process.command")

    /**
     * The full command used to launch the process as a single string representing the full command.
     * On Windows, can be set to the result of `GetCommandLineW`. Do not set this if you have to
     * assemble it just for monitoring; use `process.command_args` instead.
     */
    val PROCESS_COMMAND_LINE: AttributeKey<String> = stringKey("process.command_line")

    /**
     * All the command arguments (including the command/executable itself) as received by the
     * process. On Linux-based systems (and some other Unixoid systems supporting procfs), can be
     * set according to the list of null-delimited strings extracted from `proc/[pid]/cmdline`. For
     * libc-based executables, this would be the full argv vector passed to `main`.
     */
    val PROCESS_COMMAND_ARGS: AttributeKey<List<String>> = stringArrayKey("process.command_args")

    /** The username of the user that owns the process. */
    val PROCESS_OWNER: AttributeKey<String> = stringKey("process.owner")

    /**
     * The name of the runtime of this process. For compiled native binaries, this SHOULD be the
     * name of the compiler.
     */
    val PROCESS_RUNTIME_NAME: AttributeKey<String> = stringKey("process.runtime.name")

    /**
     * The version of the runtime of this process, as returned by the runtime without modification.
     */
    val PROCESS_RUNTIME_VERSION: AttributeKey<String> = stringKey("process.runtime.version")

    /**
     * An additional description about the runtime of the process, for example a specific vendor
     * customization of the runtime environment.
     */
    val PROCESS_RUNTIME_DESCRIPTION: AttributeKey<String> = stringKey("process.runtime.description")

    /**
     * Logical name of the service.
     *
     * Notes:
     *
     * * MUST be the same for all instances of horizontally scaled services. If the value was not
     * specified, SDKs MUST fallback to `unknown_service:` concatenated with
     * [`process.executable.name`](process.md#process), e.g. `unknown_service:bash`. If
     * `process.executable.name` is not available, the value MUST be set to `unknown_service`.
     */
    val SERVICE_NAME: AttributeKey<String> = stringKey("service.name")

    /**
     * A namespace for `service.name`.
     *
     * Notes:
     *
     * * A string value having a meaning that helps to distinguish a group of services, for example
     * the team name that owns a group of services. `service.name` is expected to be unique within
     * the same namespace. If `service.namespace` is not specified in the Resource then
     * `service.name` is expected to be unique for all services that have no explicit namespace
     * defined (so the empty/unspecified namespace is simply one more valid namespace). Zero-length
     * namespace string is assumed equal to unspecified namespace.
     */
    val SERVICE_NAMESPACE: AttributeKey<String> = stringKey("service.namespace")

    /**
     * The string ID of the service instance.
     *
     * Notes:
     *
     * * MUST be unique for each instance of the same `service.namespace,service.name` pair (in
     * other words `service.namespace,service.name,service.instance.id` triplet MUST be globally
     * unique). The ID helps to distinguish instances of the same service that exist at the same
     * time (e.g. instances of a horizontally scaled service). It is preferable for the ID to be
     * persistent and stay the same for the lifetime of the service instance, however it is
     * acceptable that the ID is ephemeral and changes during important lifetime events for the
     * service (e.g. service restarts). If the service has no inherent unique ID that can be used as
     * the value of this attribute it is recommended to generate a random Version 1 or Version 4 RFC
     * 4122 UUID (services aiming for reproducible UUIDs may also use Version 5, see RFC 4122 for
     * more recommendations).
     */
    val SERVICE_INSTANCE_ID: AttributeKey<String> = stringKey("service.instance.id")

    /** The version string of the service API or implementation. */
    val SERVICE_VERSION: AttributeKey<String> = stringKey("service.version")

    /** The name of the telemetry SDK as defined above. */
    val TELEMETRY_SDK_NAME: AttributeKey<String> = stringKey("telemetry.sdk.name")

    /** The language of the telemetry SDK. */
    val TELEMETRY_SDK_LANGUAGE: AttributeKey<String> = stringKey("telemetry.sdk.language")

    /** The version string of the telemetry SDK. */
    val TELEMETRY_SDK_VERSION: AttributeKey<String> = stringKey("telemetry.sdk.version")

    /** The version string of the auto instrumentation agent, if used. */
    val TELEMETRY_AUTO_VERSION: AttributeKey<String> = stringKey("telemetry.auto.version")

    /** The name of the web engine. */
    val WEBENGINE_NAME: AttributeKey<String> = stringKey("webengine.name")

    /** The version of the web engine. */
    val WEBENGINE_VERSION: AttributeKey<String> = stringKey("webengine.version")

    /** Additional description of the web engine (e.g. detailed version and edition information). */
    val WEBENGINE_DESCRIPTION: AttributeKey<String> = stringKey("webengine.description")

    // Enum definitions
    object CloudProviderValues {
        /** Alibaba Cloud. */
        const val ALIBABA_CLOUD = "alibaba_cloud"

        /** Amazon Web Services. */
        const val AWS = "aws"

        /** Microsoft Azure. */
        const val AZURE = "azure"

        /** Google Cloud Platform. */
        const val GCP = "gcp"
    }

    object CloudPlatformValues {
        /** Alibaba Cloud Elastic Compute Service. */
        const val ALIBABA_CLOUD_ECS = "alibaba_cloud_ecs"

        /** Alibaba Cloud Function Compute. */
        const val ALIBABA_CLOUD_FC = "alibaba_cloud_fc"

        /** AWS Elastic Compute Cloud. */
        const val AWS_EC2 = "aws_ec2"

        /** AWS Elastic Container Service. */
        const val AWS_ECS = "aws_ecs"

        /** AWS Elastic Kubernetes Service. */
        const val AWS_EKS = "aws_eks"

        /** AWS Lambda. */
        const val AWS_LAMBDA = "aws_lambda"

        /** AWS Elastic Beanstalk. */
        const val AWS_ELASTIC_BEANSTALK = "aws_elastic_beanstalk"

        /** Azure Virtual Machines. */
        const val AZURE_VM = "azure_vm"

        /** Azure Container Instances. */
        const val AZURE_CONTAINER_INSTANCES = "azure_container_instances"

        /** Azure Kubernetes Service. */
        const val AZURE_AKS = "azure_aks"

        /** Azure Functions. */
        const val AZURE_FUNCTIONS = "azure_functions"

        /** Azure App Service. */
        const val AZURE_APP_SERVICE = "azure_app_service"

        /** Google Cloud Compute Engine (GCE). */
        const val GCP_COMPUTE_ENGINE = "gcp_compute_engine"

        /** Google Cloud Run. */
        const val GCP_CLOUD_RUN = "gcp_cloud_run"

        /** Google Cloud Kubernetes Engine (GKE). */
        const val GCP_KUBERNETES_ENGINE = "gcp_kubernetes_engine"

        /** Google Cloud Functions (GCF). */
        const val GCP_CLOUD_FUNCTIONS = "gcp_cloud_functions"

        /** Google Cloud App Engine (GAE). */
        const val GCP_APP_ENGINE = "gcp_app_engine"
    }

    object AwsEcsLaunchtypeValues {
        /** ec2. */
        const val EC2 = "ec2"

        /** fargate. */
        const val FARGATE = "fargate"
    }

    object HostArchValues {
        /** AMD64. */
        const val AMD64 = "amd64"

        /** ARM32. */
        const val ARM32 = "arm32"

        /** ARM64. */
        const val ARM64 = "arm64"

        /** Itanium. */
        const val IA64 = "ia64"

        /** 32-bit PowerPC. */
        const val PPC32 = "ppc32"

        /** 64-bit PowerPC. */
        const val PPC64 = "ppc64"

        /** 32-bit x86. */
        const val X86 = "x86"
    }

    object OsTypeValues {
        /** Microsoft Windows. */
        const val WINDOWS = "windows"

        /** Linux. */
        const val LINUX = "linux"

        /** Apple Darwin. */
        const val DARWIN = "darwin"

        /** FreeBSD. */
        const val FREEBSD = "freebsd"

        /** NetBSD. */
        const val NETBSD = "netbsd"

        /** OpenBSD. */
        const val OPENBSD = "openbsd"

        /** DragonFly BSD. */
        const val DRAGONFLYBSD = "dragonflybsd"

        /** HP-UX (Hewlett Packard Unix). */
        const val HPUX = "hpux"

        /** AIX (Advanced Interactive eXecutive). */
        const val AIX = "aix"

        /** Oracle Solaris. */
        const val SOLARIS = "solaris"

        /** IBM z/OS. */
        const val Z_OS = "z_os"
    }

    object TelemetrySdkLanguageValues {
        /** cpp. */
        const val CPP = "cpp"

        /** dotnet. */
        const val DOTNET = "dotnet"

        /** erlang. */
        const val ERLANG = "erlang"

        /** go. */
        const val GO = "go"

        /** java. */
        const val JAVA = "java"

        /** nodejs. */
        const val NODEJS = "nodejs"

        /** php. */
        const val PHP = "php"

        /** python. */
        const val PYTHON = "python"

        /** ruby. */
        const val RUBY = "ruby"

        /** webjs. */
        const val WEBJS = "webjs"
    }
}
