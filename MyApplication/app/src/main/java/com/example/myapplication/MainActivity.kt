package com.example.myapplication

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.myapplication.ui.theme.MyApplicationTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException
import java.io.OutputStream
import java.util.*



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    Greeting("Android")
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    val counter = remember { mutableStateOf(0) } // Variable para el contador
    val context = LocalContext.current
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "Hello $name!")
        Button(
            onClick = {
                counter.value++ // Incrementar el contador al hacer clic en el botón
                printTicket(context) // Pasar el contexto de la actividad a la función printTicket()
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(text = "Clicks: ${counter.value}")
        }
    }
}



@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyApplicationTheme {
        Greeting("Android")
    }
}


private fun printTicket(context: Context) {
    val printerAddress = "XX:XX:XX:XX:XX:XX" // Dirección MAC de la impresora Bluetooth

    val adapter = BluetoothAdapter.getDefaultAdapter()
    val printer = adapter.getRemoteDevice(printerAddress)

    val uuid: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    val socket: BluetoothSocket = printer.createRfcommSocketToServiceRecord(uuid)

    try {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        socket.connect()

        val outputStream: OutputStream = socket.outputStream
        val ticketContent = getTicketContent() // Obtiene el contenido del ticket en formato PDF

        outputStream.write(ticketContent.toByteArray())

        outputStream.close()
        socket.close()
    } catch (e: IOException) {
        e.printStackTrace()
    }
}
private fun getTicketContent(): String {
    // Obtener el contenido del archivo PDF del ticket
    val ticketContent: String = readPDFFile("ruta/al/archivo.pdf")

    // Obtener la hora actual
    val currentTime = Calendar.getInstance().time

    // Obtener el número de folio
    val ticketNumber = getNextTicketNumber()

    // Realizar los reemplazos en el contenido del ticket
    val formattedContent = ticketContent.replace("{TIME}", currentTime.toString())
        .replace("{TICKET_NUMBER}", ticketNumber.toString())

    return formattedContent
}

private fun readPDFFile(filePath: String): String {
    // Implementa la lógica para leer el contenido del archivo PDF del ticket
    // Aquí deberías usar una biblioteca adecuada para leer el contenido del archivo PDF
    // y devolverlo como una cadena de texto.
    // A modo de ejemplo, se asume que ya tienes una función `readFile` para leer archivos:

    val fileContent: String = readFile(filePath)
    return fileContent
}

private fun readFile(filePath: String): String {
    // Implementa la lógica para leer el contenido de un archivo
    // Puedes usar las clases de `java.io` para leer el contenido del archivo
    // y devolverlo como una cadena de texto.
    // Aquí se muestra un ejemplo básico usando `java.io.BufferedReader`:

    val file = File(filePath)
    val bufferedReader = BufferedReader(FileReader(file))

    val content = StringBuilder()
    var line: String? = bufferedReader.readLine()

    while (line != null) {
        content.append(line)
        line = bufferedReader.readLine()
    }

    bufferedReader.close()

    return content.toString()
}

private fun getNextTicketNumber(): Int {
    // Leer el número actual de folio desde un archivo o base de datos
    val currentNumber = readTicketNumberFromFile()

    // Incrementar el número de folio
    val nextNumber = currentNumber + 1

    // Guardar el nuevo número de folio en un archivo o base de datos
    saveTicketNumberToFile(nextNumber)

    return nextNumber
}

private fun readTicketNumberFromFile(): Int {
    // Implementa la lógica para leer el número actual de folio desde un archivo o base de datos
    // Por ejemplo, puedes leer el número desde un archivo de texto o utilizar una base de datos
    // En este ejemplo, se asume que ya tienes una función `readFile` para leer archivos:

    val fileContent: String = readFile("ruta/al/archivo.txt")
    val currentNumber = fileContent.toIntOrNull() ?: 0

    return currentNumber
}

private fun saveTicketNumberToFile(number: Int) {
    // Implementa la lógica para guardar el nuevo número de folio en un archivo o base de datos
    // Por ejemplo, puedes guardar el número en un archivo de texto o utilizar una base de datos
    // En este ejemplo, se muestra un ejemplo básico usando `java.io.FileWriter`:

    val file = File("ruta/al/archivo.txt")
    val fileWriter = FileWriter(file)

    fileWriter.write(number.toString())

    fileWriter.close()
}












