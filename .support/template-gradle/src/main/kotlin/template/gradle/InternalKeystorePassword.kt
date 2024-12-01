package template.gradle

import com.eygraber.ejson.Ejson
import com.eygraber.ejson.gradle.decryptSecrets
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive
import org.gradle.api.Project

fun Project.getInternalKeystorePassword() = Ejson().decryptSecrets(
  secretsFile = file("secrets.ejson").toPath(),
  userSuppliedPrivateKey = System.getenv("EJSON_DEV_BUILD_PRIVATE_KEY"),
) { json ->
  requireNotNull(
    json["internal_keystore_password"]
      ?.jsonPrimitive
      ?.contentOrNull,
  )
}
