package filters
// In file: /app/Filters.scala
import javax.inject.Inject
import play.api.http.DefaultHttpFilters
import play.filters.cors.CORSFilter
class Filters @Inject()(corsFilter: CORSFilter) extends DefaultHttpFilters(corsFilter)