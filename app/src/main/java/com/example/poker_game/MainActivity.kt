package com.example.poker_game

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.poker_game.ui.theme.Poker_GameTheme
import com.chaquo.python.Python
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import android.content.Context
import com.chaquo.python.PyObject
import org.json.JSONObject

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()


        setContent {
            /*
            // Pythonの初期化
            if (!Python.isStarted()) {
                Python.start(AndroidPlatform(LocalContext.current))
            }
             */

            PokerGameApp()
        }

    }
}

@Composable
fun PokerGameApp() {
    val py = Python.getInstance()
    val pokerGame = py.getModule("PokerGame")

    pokerGame.callAttr("shuffle_deck")

    // 手札と役の状態管理
    val hand = remember { mutableStateListOf<String>() }
    var handRank by remember { mutableStateOf("") }

    // 初期の手札を引く処理
    LaunchedEffect(Unit) {
        // 関数get_hand_with_rankから手札と役を取得
        val resultString = pokerGame.callAttr("get_hand_with_rank").toString()
        val result = JSONObject(resultString)
        println("result: $result") // ログに出力して確認

        // resultから手札のみcardListに格納
        val cardList = result.getJSONArray("hand").let { jsonArray ->
            List(jsonArray.length()) { jsonArray.getString(it) }
        }
        println("cardList: $cardList") // ログに出力して確認

        // resultから役をhandRankのみ格納
        handRank = result.getString("rank")
        println("handRank: $handRank") // ログに出力して確認

        hand.clear()
        hand.addAll(cardList)
    }

    // ゲームのUI表示
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(text = "Player 1", style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(16.dp))

        // 手札のカードを表示
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            hand.forEach { card ->
                CardImage(card) // カード画像を表示
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 役の表示
        Text(text = "Hand Rank: $handRank", style = MaterialTheme.typography.bodyMedium)

        Spacer(modifier = Modifier.height(24.dp))

        // 新しいカードを引くボタン
        Button(onClick = {
            // 関数get_hand_with_rankから手札と役を取得
            val resultString = pokerGame.callAttr("get_hand_with_rank").toString()
            val result = JSONObject(resultString)
            println("result: $result") // ログに出力して確認

            // resultから手札のみcardListに格納
            val cardList = result.getJSONArray("hand").let { jsonArray ->
                List(jsonArray.length()) { jsonArray.getString(it) }
            }
            println("cardList: $cardList") // ログに出力して確認

            // resultから役をhandRankのみ格納
            handRank = result.getString("rank")
            println("handRank: $handRank") // ログに出力して確認

            hand.clear()
            hand.addAll(cardList)
        }) {
            Text("Draw Cards")
        }
    }
}

@Composable
fun CardImage(cardName: String) {
    val context = LocalContext.current
    val cardResId = getDrawableResourceByName(context, cardName)
    Image(
        painter = painterResource(id = cardResId),
        contentDescription = cardName,
        modifier = Modifier
            .size(64.dp)
            .padding(4.dp)
    )
}

// DrawableリソースIDを名前で取得するヘルパー関数
@Composable
fun getDrawableResourceByName(context: Context, name: String): Int {
    return context.resources.getIdentifier(name, "drawable", context.packageName)
}

@Preview(showBackground = true)
@Composable
fun PokerGamePreview() {
    Poker_GameTheme {
        PokerGameApp()
    }
}

