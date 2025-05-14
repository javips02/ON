import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@Composable
fun FunctionalitiesScreen(name: String?) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (!name.isNullOrBlank()) "Welcome, $name!" else "Welcome!",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Text(
                text = "This is the functionalities screen.",
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Button(
                onClick = {
                    scope.launch {
                        val result = pingOnce("1.1.1.1")
                        Toast.makeText(context, result, Toast.LENGTH_LONG).show()
                    }
                },
                modifier = Modifier.padding(8.dp)
            ) {
                Text("Ping 1.1.1.1")
            }
        }
    }
}


suspend fun pingOnce(ip: String): String = withContext(Dispatchers.IO) {
    try {
        val pingPath = "/system/bin/ping"
        val cmd = arrayOf(pingPath, "-c", "1", "-W", "1", ip) // -W 1 = 1 sec timeout
        val process = Runtime.getRuntime().exec(cmd)

        val output = process.inputStream.bufferedReader().readText()
        val errorOutput = process.errorStream.bufferedReader().readText()
        val exitCode = process.waitFor()

        return@withContext when (exitCode) {
            0 -> {
                // Buscar latencia si quieres
                val timeRegex = Regex("time=(\\d+(\\.\\d+)?)")
                val match = timeRegex.find(output)
                val latency = match?.groups?.get(1)?.value
                if (latency != null)
                    "Ping successful: $ip reachable in $latency ms."
                else
                    "Ping successful: $ip reachable."
            }
            else -> "Ping failed (code $exitCode). Error: $errorOutput"
        }
    } catch (e: Exception) {
        return@withContext "Error: ${e.message}"
    }
}
