package routes

import domain.Doctors
import domain.Timeslots
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.*
import model.Country
import model.NewDoctorTimeslots
import model.TimeslotId

fun Route.timeslots() {

	route("/timeslots") {

		post("/") {
			val newDoctorTimeslots = call.receive<NewDoctorTimeslots>()

			call.respond(HttpStatusCode.Created,
				Doctors
					.with(newDoctorTimeslots.doctorId)
					.bindTimeslots(newDoctorTimeslots.timeslots))
		}

		get("/count") {
			call.respond(Timeslots.countAvailableTimeslots())
		}

		get("/available") {
			call.respond(Timeslots.findNextTimeslots(Country.SERBIA))
		}

		put("{id}/reserve") {
			val id = call.parameters["id"]?.toInt() ?: throw IllegalStateException("ID missing")
			call.respond(HttpStatusCode.NoContent, Timeslots.with(TimeslotId(id)).reserve());
		}
	}

}
