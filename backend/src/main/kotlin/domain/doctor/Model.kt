package domain.doctor

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import domain.Id
import domain.user.UserId
import org.jetbrains.exposed.dao.id.EntityID

data class DoctorId @JsonCreator(mode = JsonCreator.Mode.DELEGATING) constructor(
	@JsonValue override val value: Int
) : Id()

fun Int.toDoctorId(): DoctorId {
	return DoctorId(this)
}

fun String.toDoctorId(): DoctorId {
	return DoctorId(this.toInt())
}

fun EntityID<Int>.toDoctorId(): DoctorId {
	return DoctorId(this.value);
}

fun UserId.toDoctorId(): DoctorId {
	return this.value.toDoctorId()
}

enum class DoctorSex(val value: Boolean) {
	MALE(true), FEMALE(false);

	companion object {
		fun of(value: Boolean): DoctorSex = if (value) MALE else FEMALE;
	}
}

enum class DoctorCertificate(val value: Int) {
	NONE(0), NATIONAL(1), INTERNATIONAL(2);

	companion object {
		fun of(value: Int): DoctorCertificate = when (value) {
			0 -> NONE
			1 -> NATIONAL
			2 -> INTERNATIONAL
			else -> throw Error("Invalid certificate")
		}
	}
}

data class DoctorData(
	val name: String,
	val email: String,
	val sex: DoctorSex,
	val year: Int,
	val education: Int,
	val occupation: Int,
	val occupation2: String = "",
	val occupationSpec: String = "",
	val certificate: DoctorCertificate,
	val modalitet: Int,
	val modalitet2: String = "",
	val phone: String,
	val zoom: String
)

data class Doctor(
	val id: DoctorId,
	val data: DoctorData,
	val userId: Int
)
