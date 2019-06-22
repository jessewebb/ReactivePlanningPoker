package filters

import akka.stream.Materializer
import javax.inject.Inject
import play.api.Logging
import play.api.mvc.{Filter, RequestHeader, Result}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class RequestResultLoggingFilter @Inject() (implicit val mat: Materializer)
  extends Filter with Logging {

  def apply(nextFilter: RequestHeader => Future[Result])
           (request: RequestHeader): Future[Result] = {
    val startTime = System.currentTimeMillis()
    val resultFuture = nextFilter(request)
    resultFuture.map { result =>
      val endTime = System.currentTimeMillis()
      val requestTime = endTime - startTime
      val msg = s"${request.method} ${request.uri} ${requestTime}ms ${request.remoteAddress} ${result.header.status}"
      logger.info(msg)
    }
    resultFuture
  }
}
