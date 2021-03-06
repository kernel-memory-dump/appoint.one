package domain.doctor

import DateTime
import domain.timeslot.*
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.select
import server.DatabaseFactory.dbtx

class DoctorTimeslots(private val doctorId: DoctorId) {

	suspend fun listTimeslots() = dbtx {
		TimeslotsTable.select {
			TimeslotsTable.doctorIdRef eq doctorId.value
		}
			.sortedWith(compareBy { it[TimeslotsTable.datetime] })
			.reversed()
			.map { it.toTimeslot() }
	}

	suspend fun bindTimeslots(dateTimeList: List<DateTime>) = dbtx {
		val existingTimeslots = listTimeslots()

		dateTimeList
			.filter {
				existingTimeslots.none { existing ->
					existing.datetime == it
				}
			}
			.map {
				NewTimeslot(datetime = it, doctorId = doctorId)
			}
			.map { newTimeslot ->
				TimeslotsTable.insertAndGetId {
					newTimeslot.data(it)
				}.toTimeslotId()
			}
	}

	suspend fun bindAndReturnTimeslots(dateTimeList: List<DateTime>) = dbtx {
		val timeslots = bindTimeslots(dateTimeList).map { it.value }

		TimeslotsTable.select { TimeslotsTable.id inList timeslots }.map { it.toTimeslot() }
	}


}
