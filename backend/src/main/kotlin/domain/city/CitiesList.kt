package domain.city

import org.jetbrains.exposed.sql.selectAll
import server.DatabaseFactory.dbtx

/**
 * Various lists of cities.
 */
object CitiesList {

	suspend fun allCities(): List<CityModel> = dbtx {
		CityTable.selectAll().map { it.toCity() }
	}
}
