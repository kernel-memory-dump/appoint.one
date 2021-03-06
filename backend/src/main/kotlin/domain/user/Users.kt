package domain.user

import auth.BCryptHasher
import domain.doctor.*
import org.jetbrains.exposed.sql.insertAndGetId
import server.DatabaseFactory.dbtx

object Users {

	suspend fun addUser(user: NewUser): UserId = dbtx {
		val userId = UsersTable.insertAndGetId {
			it[name] = user.name
			it[password] = BCryptHasher.hashPassword(user.password)
			it[role] = user.role.value
		}
		userId.toUserId()
	}

	suspend fun addDoctor(userDoctor: NewDoctorUser): DoctorId = dbtx {
		userDoctor.assertValidDoctorRegCode()

		val uid = UsersTable.insertAndGetId {
			NewUser(
				name = userDoctor.name,
				password = userDoctor.password,
				role = UserRole.DOC
			).data(it)
		}

		DoctorsTable.insertAndGetId {
			userDoctor.doctor.data(it)
			it[id] = uid
			it[userIdRef] = uid.value
		}
			.toDoctorId()
	}

	suspend fun addAndGetDoctor(userDoctor: NewDoctorUser): Doctor = dbtx {
		val doctorId = addDoctor(userDoctor)

		DoctorById(doctorId).existing()
	}

}
