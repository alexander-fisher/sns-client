package uk.gov.hmrc.snsclient.aws.sns

import javax.inject.{Inject, Singleton}

import com.amazonaws.auth.{AWSCredentials, AWSCredentialsProvider, AWSStaticCredentialsProvider}
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration
import com.amazonaws.services.sns.{AmazonSNSAsync, AmazonSNSAsyncClientBuilder}

@Singleton()
class AwsSnsClientBuilder @Inject()(configuration: SnsConfiguration) {

  def credentials: AWSStaticCredentialsProvider = {
    new AWSStaticCredentialsProvider(new AWSCredentials {
      override def getAWSAccessKeyId: String = configuration.accessKey
      override def getAWSSecretKey: String = configuration.secret
    })
  }

  def endpoint: EndpointConfiguration = {
    new EndpointConfiguration(configuration.serviceEndpoint, configuration.signingRegion)
  }

  private def getInstance(credentials:AWSCredentialsProvider, endpointConfig:EndpointConfiguration): AmazonSNSAsync = {
    AmazonSNSAsyncClientBuilder
      .standard
      .withCredentials(credentials)
      .withEndpointConfiguration(endpointConfig)
      .build()
  }

  def getInstance: AmazonSNSAsync = {
    getInstance(credentials, endpoint)
  }
}