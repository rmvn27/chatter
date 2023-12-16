package chatter.lib.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.util.UUID

// since UUID is a java type we have to provide a custom serializer
// for it. For this the UUID is just encoded and decoded as a string
object UUIDSerializer : KSerializer<UUID> {
    override val descriptor = String.serializer().descriptor

    override fun deserialize(decoder: Decoder): UUID =
        UUID.fromString(decoder.decodeString())

    override fun serialize(encoder: Encoder, value: UUID) =
        encoder.encodeString(value.toString())
}
