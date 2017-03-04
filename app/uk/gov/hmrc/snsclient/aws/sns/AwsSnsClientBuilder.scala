package uk.gov.hmrc.snsclient.aws.sns

import com.amazonaws.auth.{AWSCredentials, AWSCredentialsProvider, AWSStaticCredentialsProvider}
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration
import com.amazonaws.services.sns.{AmazonSNSAsync, AmazonSNSAsyncClientBuilder}

trait AwsSnsClientBuilder {

  def credentials(configuration: SnsConfiguration): AWSStaticCredentialsProvider = {
    new AWSStaticCredentialsProvider(new AWSCredentials {
      override def getAWSAccessKeyId: String = configuration.accessKey
      override def getAWSSecretKey: String = configuration.secret
    })
  }

  def endpoint(configuration: SnsConfiguration): EndpointConfiguration = {
    new EndpointConfiguration(configuration.serviceEndpoint, configuration.signingRegion)
  }

  def snsClient(credentials:AWSCredentialsProvider, endpointConfig:EndpointConfiguration): AmazonSNSAsync = {
    AmazonSNSAsyncClientBuilder
      .standard
      .withCredentials(credentials)
      .withEndpointConfiguration(endpointConfig)
      .build()
  }

  def snsClient(configuration:SnsConfiguration): AmazonSNSAsync = {
    snsClient(credentials(configuration), endpoint(configuration))
  }
}