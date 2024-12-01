package com.ilya.myspb.android.ChatwithBot

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.auth.api.identity.Identity
import com.ilya.MeetingMap.Mine_menu.Map_Activity
import com.ilya.codewithfriends.presentation.profile.ID
import com.ilya.codewithfriends.presentation.sign_in.GoogleAuthUiClient
import com.ilya.myspb.android.ChatwithBot.ModelData.ChatInfo
import com.ilya.myspb.android.ChatwithBot.ModelData.ChatMessage
import com.ilya.myspb.android.ChatwithBot.ModelData.ChatRequest
import com.ilya.myspb.android.ChatwithBot.ModelData.sendCreateChatRequest
import com.ilya.myspb.android.ChatwithBot.ModelData.sendMessage

import com.ilya.myspb.android.ChatwithBot.RESTservis.fetchMessages
import com.ilya.myspb.android.ChatwithBot.RESTservis.getPublicMarker
import com.ilya.myspb.android.ChatwithBot.ui.theme.MySPBTheme
import com.ilya.myspb.android.R
import com.ilya.reaction.logik.PreferenceHelper
import com.larkes.hsesurvey.domain.models.CategoryTypes
import com.larkes.hsesurvey.ui.components.DropDownMenuComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.UUID

class ChatwithBoot : ComponentActivity() {

    private val googleAuthUiClient by lazy {
        GoogleAuthUiClient(
            context = applicationContext,
            oneTapClient = Identity.getSignInClient(applicationContext)
        )
    }

    private var chattid by mutableStateOf("")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val uid = ID(userData = googleAuthUiClient.getSignedInUser())

        setContent {

            var chatInfoList by remember { mutableStateOf<List<ChatInfo>>(emptyList()) }


            // Запуск корутины для получения данных
            LaunchedEffect(Unit) {
                try {
                    // Получение данных
                    chatInfoList = getPublicMarker("$uid") // Сохранение полученных данных в состояние
                    Log.d("MapMarker_getMarker", "Полученные данные: $chatInfoList")
                } catch (e: Exception) {
                    Log.e("MapMarker_getMarker", "Ошибка при получении данных", e)
                    // Обработка ошибок
                }
            }


            MySPBTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "start") {
                
                    composable("start") {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color(0xffe7f8ff)) // Опционально для задания фона
                        ) {
                            // Верхняя часть экрана с элементами
                            Column(
                                modifier = Modifier
                                    .weight(1f) // Заполняет доступное пространство
                                    .padding(16.dp) // Добавляет отступы от краев экрана
                            ) {
                                CommonBox()
                                Spacer(modifier = Modifier.height(20.dp))
                                ChooseUserWay()
                                Spacer(modifier = Modifier.height(20.dp))
                                Chathistory(chatInfoList, navController )
                            }

                            Spacer(modifier = Modifier.height(20.dp)) // Отступ перед нижней навигацией

                            // Нижняя навигация, закрепленная внизу экрана
                            bottombranavigation(navController)
                        }

                    }

                    composable("chat")
                    {
                        Spacer(modifier = Modifier.height(20.dp))
                        ChatScreen(uid.toString())
                    }
                    composable("newchat")
                    {
                        CategorySelection(navController, uid.toString())
                    }


                }
            }
        }
    }

    @Composable
    fun CategorySelection(navController: NavController, uid: String)    {
        val coroutineScope = rememberCoroutineScope()
        // Стейт для выбранной категории
        var selectedCategory by remember { mutableStateOf(CategoryTypes.CityLife) }

        // Функция, которая обновляет выбранную категорию
        val onCategorySelected: (CategoryTypes) -> Unit = { category ->
            selectedCategory = category
        }

        val savedatainmemory = PreferenceHelper.PreferencesManager(applicationContext)

        // Размещение компонентов в колонке
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween // Размещение с отступами сверху и снизу
        ) {
            // Вызов DropDownMenuComponent с необходимыми параметрами
            DropDownMenuComponent(
                selectedTitle = selectedCategory,
                onSelect = onCategorySelected
            )

            Spacer(modifier = Modifier.weight(1f)) // Это поможет кнопке располагаться внизу экрана

            // Кнопка снизу
            Button(
                onClick = {
                    val token = UUID.randomUUID().toString()
                    coroutineScope.launch {

                        sendCreateChatRequest(
                            uid,
                            token,
                            ChatRequest(selectedCategory.toString(), "GigaChat")
                        )
                    }
                    chattid = token
                    savedatainmemory.saveString(token, selectedCategory.toString())

                    navController.navigate("chat")
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Подтвердить выбор")
            }
        }
    }





    @Composable
    fun ChatScreen(uid: String) {
        val messages = remember { mutableStateOf<List<ChatMessage>?>(null) }
        val textState = remember { mutableStateOf("") }
        val listState = rememberLazyListState()

        // Загружаем сообщения и обновляем их каждые 10 секунд
        LaunchedEffect(Unit) {
            messages.value = fetchMessages(uid, chattid)
            while (true) {
                delay(10_000)
                messages.value = fetchMessages(uid, chattid)
            }
        }

        // Скролл к последнему сообщению после обновления списка
        LaunchedEffect(messages.value) {
            messages.value?.let {
                if (it.isNotEmpty()) {
                    listState.scrollToItem(0)
                }
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 80.dp)
            ) {
                if (messages.value != null) {
                    LazyColumn(
                        state = listState,
                        reverseLayout = true, // Переворачиваем список
                        modifier = Modifier.weight(1f)
                    ) {
                        items(messages.value!!.reversed()) { message -> // Переворачиваем порядок сообщений
                            ChatMessageCard(message)
                        }
                    }
                } else {
                    Text(
                        "Загрузка сообщений...",
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .align(Alignment.BottomCenter),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextField(
                    value = textState.value,
                    onValueChange = { textState.value = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Введите сообщение...") }
                )

                IconButton(onClick = {
                    sendMessage(uid, chattid, textState.value)
                    textState.value = ""
                }) {
                    Icon(Icons.Default.Send, contentDescription = "Отправить")
                }
            }
        }
    }

    @Composable
    fun ChatMessageCard(message: ChatMessage) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            // Отображение вашего сообщения (message.message) слева
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                MessageBubble(
                    text = message.message,
                    backgroundColor = Color(0xFFEEEEEE),
                    textColor = Color.Black
                )
            }

            // Отображение ответа GPT (message.gptResponse) справа
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                MessageBubble(
                    text = message.gptResponse,
                    backgroundColor = Color(0xFF315FF3),
                    textColor = Color.White
                )
            }
        }
    }

    @Composable
    fun MessageBubble(text: String, backgroundColor: Color, textColor: Color) {
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors (backgroundColor),
            modifier = Modifier
                .wrapContentWidth()
                .padding(horizontal = 8.dp)
        ) {
            Text(
                text = text,
                color = textColor,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(8.dp)
            )
        }
    }


    // Функция для форматирования временного штампа
    fun formatTimestamp(timestamp: String): String {
        return try {
            val instant = Instant.ofEpochMilli(timestamp.toLong())
            val formatter = DateTimeFormatter.ofPattern("HH:mm")
                .withZone(ZoneId.systemDefault())
            formatter.format(instant)
        } catch (e: Exception) {
            timestamp // Вернуть исходное значение, если не удалось преобразовать
        }
    }



    @Preview
    @Composable
    fun CommonBox() {
        val font = FontFamily(
            Font(R.font.open_sans_semi_condensed_regular, FontWeight.Normal),
        )
        val textColor_theme = if (isSystemInDarkTheme()) Color.White else Color.Black
        val backgroundColor = if (isSystemInDarkTheme()) Color.Black else Color(0xfffffeff)


        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .padding(start = 20.dp, end = 20.dp)
        ) {
            Box(modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
            ) {
                Text(text = "MYSPB chatbot",
                    textAlign = TextAlign.Start,
                    fontSize = 24.sp,
                    color = textColor_theme,
                    fontWeight = FontWeight.Bold,
                    fontFamily = font,
                    modifier = Modifier
                        .padding(start = 10.dp, top = 30.dp)
                        .fillMaxWidth()
                        .align(Alignment.CenterStart)
                )

            }


        }
    }


    @Composable
    fun ChooseUserWay(

    ){
        val font = FontFamily(
            Font(R.font.open_sans_semi_condensed_regular, FontWeight.Normal),
        )
        val textColor_theme = if (isSystemInDarkTheme()) Color.White else Color.Black
        val backgroundColor = if (isSystemInDarkTheme()) Color.Black else Color.White

        Row(modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
        )
        {

            val font = FontFamily(
                Font(R.font.open_sans_semi_condensed_regular, FontWeight.Normal),
            )
            val textColor_theme = if (isSystemInDarkTheme()) Color.White else Color.Black
            val backgroundColor = if (isSystemInDarkTheme()) Color.Black else Color.White
            val buttonColor = if (isSystemInDarkTheme())
             Color(0xff5cd8fa)
            else  Color(0xff0fffeff)


            Button(
                modifier = Modifier
                    .weight(0.5f)
                   // .padding(start = 10.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .height(60.dp),
                colors = ButtonDefaults.buttonColors(buttonColor),
                shape = RoundedCornerShape(20.dp),
                onClick = {

                }
            )
            {
                Text(stringResource(id = R.string.turist),
                    color = textColor_theme,
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = font
                    )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Button(
                modifier = Modifier
                    .weight(0.5f)
                    .clip(RoundedCornerShape(10.dp))
                  //  .padding(start = 10.dp)
                    .height(60.dp),
                colors = ButtonDefaults.buttonColors(buttonColor),
                shape = RoundedCornerShape(20.dp),
                onClick = {

                }
            )
            {
                Text(stringResource(id = R.string.citenzin),
                        color = textColor_theme,
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = font
                )
            }
        }
    }


    @Composable
    fun Chathistory(
        chats: List<ChatInfo>,
        navController: NavController

    ) {

        val font = FontFamily(
            Font(R.font.open_sans_semi_condensed_regular, FontWeight.Normal),
        )

        // Определяем цвета для светлой и темной темы
        val textColor = if (isSystemInDarkTheme()) Color.White else Color.Black
        val backgroundColor = if (isSystemInDarkTheme()) Color.Black else Color.White
        val buttonColor = if (isSystemInDarkTheme()) Color(0xff5cd8fa) else Color(0xff0fffeff)

        // Стиль для текста
        val textStyle = TextStyle(color = textColor, fontFamily = font, fontWeight = FontWeight.Bold, fontSize = 20.sp)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .background(backgroundColor)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {
                // История чатов
                LazyColumn(
                    modifier = Modifier.weight(1f)
                ) {
                    items(chats) { chat ->
                        // Здесь отображаем каждое сообщение чата
                        ChatCard(chat, textStyle, navController)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

            }
        }
    }


    @Composable
    fun ChatCard(
        chat: ChatInfo,
        textStyle: TextStyle,
        navController: NavController
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable{
                    chattid = chat.chatId
                    navController.navigate("chat")
                }
                .clip(RoundedCornerShape(10.dp))
                .padding(8.dp),
            shape = RoundedCornerShape(16.dp), // закругленные углы
            colors = CardDefaults.cardColors(containerColor = Color(0xFFe7f8ff)) // светлый фон
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(text = chat.chatName, style = textStyle)
            }
        }
    }



    @Composable
    fun bottombranavigation(
        navController: NavController
    ){
        val font = FontFamily(
            Font(R.font.open_sans_semi_condensed_regular, FontWeight.Normal),
        )
        val textColor_theme = if (isSystemInDarkTheme()) Color.White else Color.Black
        val backgroundColor = if (isSystemInDarkTheme()) Color.Black else Color.White
        val buttonColor = if (isSystemInDarkTheme())
            Color(0xff5cd8fa)
        else  Color(0xff0fffeff)
        Row(modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .padding(start = 16.dp, end = 16.dp)
        )
        {
                    Button(
                        modifier = Modifier
                            .weight(0.5f)
                            .clip(RoundedCornerShape(10.dp))
                            .height(60.dp),
                        colors = ButtonDefaults.buttonColors(buttonColor),
                        shape = RoundedCornerShape(20.dp),
                        onClick = {
                            val intent = Intent(this@ChatwithBoot, Map_Activity::class.java)
                            startActivity(intent)
                        }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically, // Центрирует иконку и текст по вертикали
                            horizontalArrangement = Arrangement.Center // Центрирует содержимое кнопки по горизонтали
                        ) {
                            Icon(
                                imageVector = Icons.Default.Map, // Иконка карты
                                contentDescription = null, // Описание для доступности
                                modifier = Modifier
                                    .size(40.dp) // Размер иконки
                                    .padding(end = 8.dp), // Отступ справа от иконки
                                tint = textColor_theme // Цвет иконки
                            )
                            Text(
                                text = stringResource(id = R.string.AIMap),
                                fontFamily = font,
                                fontSize = 14.sp,
                                color = textColor_theme
                            )
                        }
                    }

            Spacer(modifier = Modifier.width(10.dp))
            Button(
                modifier = Modifier
                    .weight(0.5f)
                    .clip(RoundedCornerShape(10.dp))
                    .height(60.dp),
                colors = ButtonDefaults.buttonColors(buttonColor),
                shape = RoundedCornerShape(20.dp),
                onClick = {
                    navController.navigate("newchat")
                }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically, // Центрирует иконку и текст по вертикали
                    horizontalArrangement = Arrangement.Center // Центрирует содержимое кнопки по горизонтали
                ) {
                    Icon(
                        imageVector = Icons.Default.AddCircle, // Стандартная иконка AddCircle
                        contentDescription = null, // Описание для доступности
                        modifier = Modifier
                            .size(40.dp) // Размер иконки
                            .padding(end = 8.dp), // Отступ справа от иконки
                        tint = textColor_theme // Цвет иконки
                    )
                    Text(
                        text = stringResource(id = R.string.NewChat),
                        fontFamily = font,
                        fontSize = 14.sp,
                        color = textColor_theme
                    )
                }
            }

        }
    }
}






