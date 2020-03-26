package web

import domain.Doctors
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.post
import io.ktor.routing.route
import model.NewDoctorTimeslots

fun Route.timeslots() {

    route("/timeslot") {

        post("/") {
            val newDoctorTimeslots = call.receive<NewDoctorTimeslots>()

            call.respond(HttpStatusCode.Created,
                Doctors
                    .with(newDoctorTimeslots.doctorId)
                    .bindTimeslots(newDoctorTimeslots.timeslots))
        }
    }

}