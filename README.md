# GCash Java Spring SDK

[![Java CI with Gradle](https://github.com/vincenzolabs/GCash-Java-Spring-SDK/actions/workflows/gradle-build.yml/badge.svg)](https://github.com/vincenzolabs/GCash-Java-Spring-SDK/actions/workflows/gradle-build.yml)

The `GCash-Java-Spring-SDK` is a client library written in Java 17 and Spring Boot 3 for invoking GCash (formerly GCash)
payment services.

## Development Environment

- Install Amazon Corretto 17 or newer from https://aws.amazon.com/corretto/.
- Install IDE with Gradle plugin.

### Compilation and Unit Testing

- Run `./gradlew clean build`

### Component Testing

- Run `./gradlew clean componentTest`

### Integration Testing

- Run `./gradlew clean integrationTest`

## Usage

### Gradle

- In your `build.gradle`, add the dependency:

```groovy
implementation "org.vincenzolabs:gcash-java-spring-sdk:$gcashVersion"
```

### Maven

- In your `pom.xml`, add the dependency:

```xml

<dependency>
    <groupId>org.vincenzolabs</groupId>
    <artifactId>gcash-java-spring-sdk</artifactId>
    <version>${gcash.version}</version>
</dependency>
```

### Configuration

- In your `application-test.yaml`, set the following properties:

```yaml
gcash:
  signing:
    publicKey:
    privateKey:
    keyVersion: 0
    algorithm: RS256
  paymentGatewayUrl:
  clientId:
  zoneId: Asia/Manila
```

- In your `application.yaml`, specify your production keys and point the payment gateway URL
  to `https://pg.paygcash.com`.

### Client

- In your client code, inject `GCashV1Client`.
- Generate access token:

```java
AccessTokenRequest accessTokenRequest = AccessTokenRequest.builder()
        .grantType(GrantType.AUTHORIZATION_CODE)
        .referenceClientId("305XST2CSG0N4P0xxxx")
        .authCode("2810111301lGZcM9CjlF91WH00039190xxxx")
        .extendInfo("{\"customerBelongsTo\":\"siteNameExample\"}")
        .build();

Mono<AccessTokenResponse> accessTokenResponseMono = client.applyAccessToken(accessTokenRequest);
```

- To let your customer pay using their GCash wallet:

```java
PaymentRequest paymentRequest = PaymentRequest.builder()
        .partnerId("P000000000000001xxxx")
        .paymentRequestId("2019112719074101000700000077771xxxx")
        .paymentOrderTitle("SHOES")
        .productCode("PC_5800000001")
        .mcc("4399")
        .paymentAmount(Amount.builder()
            .currency("USD")
            .value("10000")
            .build())
        .paymentFactor(PaymentFactor.builder()
            .isCashierPayment(true)
            .build())
        .paymentReturnUrl("https://www.merchant.com/redirectxxx")
        .paymentNotifyUrl("https://www.merchant.com/paymentNotifyxxx")
        .extraParams(Map.of("ORDER",
                "{\"referenceOrderId\":\"ID_000001\",\"orderAmount\":\"{\"currency\":\"USD\",\"value\":\"10000\"}\"}"))
        .extendInfo("{\"customerBelongsTo\":\"siteNameExample\"}")
        .envInfo(EnvInfo.builder()
            .osType(OsType.IOS)
            .terminalType(TerminalType.APP)
            .build())
        .build();

Mono<PaymentResponse> paymentResponseMono = client.createPayment(paymentRequest);
```
