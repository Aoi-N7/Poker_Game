package com.example.poker_game

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.sp
import com.chaquo.python.PyObject
import com.chaquo.python.android.AndroidPlatform
import org.json.JSONObject
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import org.json.JSONArray

// プレイヤー情報を管理するデータクラス
data class Player(val name: String, var hand: List<String> = emptyList(), var handRank: String = "", var handRankValue: Int = 0, var handlevel: Int = 0)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            // Pythonの初期化
            if (!Python.isStarted()) {
                Python.start(AndroidPlatform(this))
            }

            PokerGameApp()
        }
    }
}

@Composable
fun PokerGameApp() {
    var currentScreen by remember { mutableStateOf("start") }
    val players = remember { mutableStateListOf<Player>() }
    val playerNames = remember { mutableStateListOf<String>() }
    var playerCount by remember { mutableStateOf(1) }
    val py = Python.getInstance()
    val pokerGame = py.getModule("PokerGame")

    when (currentScreen) {
        "start" -> Background {
            StartScreen(
                onStart = { currentScreen = "setup" },
                onSettings = { currentScreen = "settings" }
            )
        }
        /*
        "settings" -> Background {
            SettingsScreen(
                onBack = { currentScreen = "start" }
            )
        }
         */
        "setup" -> Background {
            PlayerSetupScreen(
                initialPlayerCount = playerCount,
                initialPlayerNames = playerNames,
                onPlayersSetup = { setupPlayers, count ->
                    players.clear()
                    players.addAll(setupPlayers)
                    playerNames.clear()
                    playerNames.addAll(setupPlayers.map { it.name })
                    playerCount = count
                    pokerGame.callAttr("shuffle_deck") // デッキをシャッフル
                    currentScreen = "game"
                },
                onBack = { currentScreen = "start" }
            )
        }
        "game" -> Background {
            PokerGameScreen(players) {
                currentScreen = "ranking"
            }
        }
        "ranking" -> Background {
            RankingScreen(players) {
                currentScreen = "start"
            }
        }
    }
}

@Composable
fun StartScreen(onStart: () -> Unit, onSettings: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()  // 画面全体を埋める
            .padding(40.dp),  // 外側の余白を設定
        horizontalAlignment = Alignment.CenterHorizontally,  // 水平方向に中央揃え
        verticalArrangement = Arrangement.SpaceBetween  // 垂直方向に要素間のスペースを均等に配置
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()  // 横幅を画面全体に設定
                .padding(top = 20.dp),  // 上部に余白を設定
            contentAlignment = Alignment.TopEnd  // 右上に配置
        ) {
            /*
            Image(
                painter = painterResource(id = R.drawable.setting),
                contentDescription = "設定",  // 画像の内容説明
                modifier = Modifier
                    .size(70.dp)  // 画像のサイズを設定
                    .clickable { onSettings() }  // クリック可能にして、クリック時にonSettingsを呼び出す
            )
             */
        }
        Text(
            text = "どこでも\n\n\n        ポーカー",  // 表示するテキスト
            style = MaterialTheme.typography.headlineLarge.copy(fontSize = 50.sp)  // テキストのスタイルとサイズを設定
        )

        Spacer(modifier = Modifier.height(16.dp))  // テキストとボタンの間にスペースを追加
        Button(
            onClick = onStart,  // ボタンがクリックされたときにonStartを呼び出す
            modifier = Modifier
                .width(200.dp)  // ボタンの幅を200dpに設定
                .height(60.dp)  // ボタンの高さを60dpに設定
        ) {
            Text(
                text = "スタート",  // ボタンに表示するテキスト
                style = MaterialTheme.typography.headlineLarge.copy(fontSize = 30.sp)  // テキストのスタイルとサイズを設定
            )
        }
    }
}

/*
@Composable
fun SettingsScreen(onBack: () -> Unit) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    )
    {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        )
        {
            Image(
                painter = painterResource(id = R.drawable.back),
                contentDescription = "Back",
                modifier = Modifier
                    .size(80.dp)
                    .clickable { onBack() }
            )
        }


    }
}
 */

@Composable
fun PlayerSetupScreen(
    initialPlayerCount: Int,
    initialPlayerNames: List<String>,
    onPlayersSetup: (List<Player>, Int) -> Unit,
    onBack: () -> Unit
) {
    var playerCount by remember { mutableStateOf(initialPlayerCount) }  // プレイヤー数を管理する状態
    val playerNames = remember { mutableStateListOf<String>().apply { addAll(initialPlayerNames) } }  // プレイヤー名を管理する状態

    Box(
        modifier = Modifier
            .fillMaxSize()  // 画面全体を埋める
            .padding(10.dp)  // 外側の余白を設定
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()  // 画面全体を埋める
                .padding(30.dp),  // 内側の余白を設定
            horizontalAlignment = Alignment.CenterHorizontally,  // 水平方向に中央揃え
            verticalArrangement = Arrangement.Top  // 垂直方向に上揃え
        ) {
            Spacer(modifier = Modifier.height(40.dp))  // 上部にスペースを追加

            Text(
                text = "プレイヤー情報",  // 表示するテキスト
                style = MaterialTheme.typography.headlineLarge.copy(fontSize = 40.sp)  // テキストのスタイルとサイズを設定
            )
            Spacer(modifier = Modifier.height(30.dp))  // テキストとスライダーの間にスペースを追加
            Slider(
                value = playerCount.toFloat(),  // スライダーの現在の値
                onValueChange = { playerCount = it.toInt() },  // スライダーの値が変更されたときに呼び出される
                valueRange = 1f..4f,  // スライダーの値の範囲
                steps = 2  // スライダーのステップ数
            )

            Spacer(modifier = Modifier.height(30.dp))  // スライダーとテキストの間にスペースを追加

            Text(
                text = "人数: $playerCount 人",  // 表示するテキスト
                style = MaterialTheme.typography.headlineLarge.copy(fontSize = 25.sp)  // テキストのスタイルとサイズを設定
            )
            Spacer(modifier = Modifier.height(25.dp))  // テキストと入力フィールドの間にスペースを追加

            // プレイヤー名の入力フィールドを表示
            for (i in 0 until playerCount) {
                OutlinedTextField(
                    value = if (i < playerNames.size) playerNames[i] else "",  // 入力フィールドの現在の値
                    onValueChange = { name ->
                        if (i < playerNames.size) {
                            playerNames[i] = name  // 既存のプレイヤー名を更新
                        } else {
                            playerNames.add(name)  // 新しいプレイヤー名を追加
                        }
                    },
                    label = { Text("Player ${i + 1} ") },  // 入力フィールドのラベル
                    modifier = Modifier.background(Color.White)  // 入力フィールドの背景色を白に設定
                )
                Spacer(modifier = Modifier.height(20.dp))  // 各入力フィールドの間にスペースを追加
            }

            Spacer(modifier = Modifier.height(45.dp))  // 入力フィールドとボタンの間にスペースを追加
            Button(onClick = {
                // プレイヤー名が空白の場合は"No Name"に設定
                val players = (0 until playerCount).map { index ->
                    Player(if (index < playerNames.size && playerNames[index].isNotBlank()) playerNames[index] else "No Name")
                }
                onPlayersSetup(players, playerCount)  // プレイヤー情報を設定
            },
                modifier = Modifier
                    .width(200.dp)  // ボタンの幅を200dpに設定
                    .height(60.dp)  // ボタンの高さを60dpに設定
            ) {
                Text(
                    text = "決定",  // ボタンに表示するテキスト
                    style = MaterialTheme.typography.headlineLarge.copy(fontSize = 30.sp)  // テキストのスタイルとサイズを設定
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()  // 画面全体を埋める
                .padding(20.dp),  // 外側の余白を設定
            contentAlignment = Alignment.BottomStart  // 左下に配置
        ) {
            Image(
                painter = painterResource(id = R.drawable.back),
                contentDescription = "Back",  // 画像の内容説明
                modifier = Modifier
                    .size(80.dp)  // 画像のサイズを設定
                    .clickable { onBack() }  // クリック可能にして、クリック時にonBackを呼び出す
            )
        }
    }
}

@Composable
fun PokerGameScreen(players: List<Player>, onGameEnd: (List<Player>) -> Unit) {
    val py = Python.getInstance()  // Pythonインスタンスを取得
    val pokerGame = py.getModule("PokerGame")  // PokerGameモジュールを取得

    var currentPlayerIndex by remember { mutableStateOf(0) }  // 現在のプレイヤーのインデックスを管理する状態
    val currentPlayer = players[currentPlayerIndex]  // 現在のプレイヤーを取得
    val hand = remember { mutableStateListOf<String>() }  // 手札を管理する状態
    var handRank by remember { mutableStateOf("") }  // 手札の役を管理する状態
    var handRankValue by remember { mutableStateOf(0) }  // 手札の役の値を管理する状態
    var handlevel by remember { mutableStateOf(0) }  // 手札のレベルを管理する状態
    val selectedCards = remember { mutableStateListOf<Boolean>() }  // 選択されたカードを管理する状態
    val coroutineScope = rememberCoroutineScope()  // コルーチンスコープを取得
    var isReplaceEnabled by remember { mutableStateOf(true) }  // カードの交換ボタンの有効/無効を管理する状態
    var isHandRevealed by remember { mutableStateOf(false) }  // 手札が表示されているかどうかを管理する状態

    // プレイヤーが変わるたびに手札を初期化
    LaunchedEffect(currentPlayerIndex) {
        val resultString = pokerGame.callAttr("get_hand_with_rank").toString()  // Python関数を呼び出して結果を取得
        val result = JSONObject(resultString)  // 結果をJSONオブジェクトに変換
        val cardList = result.getJSONArray("hand").let { jsonArray ->
            List(jsonArray.length()) { jsonArray.getString(it) }  // 手札のカードリストを取得
        }

        handRank = result.getString("rank")  // 手札の役を取得
        handRankValue = result.getInt("rank_value")  // 手札の役の値を取得
        handlevel = result.getInt("hand_level")  // 手札のレベルを取得
        hand.clear()  // 手札をクリア
        hand.addAll(cardList)  // 新しい手札を追加
        selectedCards.clear()  // 選択されたカードをクリア
        selectedCards.addAll(List(cardList.size) { false })  // 選択されたカードを初期化

        currentPlayer.hand = cardList  // 現在のプレイヤーの手札を更新
        currentPlayer.handRank = handRank  // 現在のプレイヤーの手札の役を更新
        currentPlayer.handRankValue = handRankValue  // 現在のプレイヤーの手札の役の値を更新
        currentPlayer.handlevel = handlevel  // 現在のプレイヤーの手札のレベルを更新

        isReplaceEnabled = true  // カードを入れ替えるボタンを有効にする
        isHandRevealed = false  // 手札を非表示にする
    }

    Column(
        modifier = Modifier
            .fillMaxSize()  // 画面全体を埋める
            .padding(20.dp),  // 外側の余白を設定
        horizontalAlignment = Alignment.CenterHorizontally,  // 水平方向に中央揃え
        verticalArrangement = Arrangement.Top  // 垂直方向に上揃え
    ) {
        Spacer(modifier = Modifier.height(80.dp))  // 上部にスペースを追加
        Text(
            text = currentPlayer.name,  // 現在のプレイヤーの名前を表示
            style = MaterialTheme.typography.headlineLarge.copy(fontSize = 50.sp)  // テキストのスタイルとサイズを設定
        )
        Spacer(modifier = Modifier.height(75.dp))  // テキストと手札の間にスペースを追加
        Row(
            modifier = Modifier.fillMaxWidth(),  // 横幅を画面全体に設定
            horizontalArrangement = Arrangement.Center  // 水平方向に中央揃え
        ) {
            // 手札のカードを表示
            if (isHandRevealed) {
                hand.forEachIndexed { index, card ->
                    CardImage(card, selectedCards[index]) {
                        selectedCards[index] = !selectedCards[index]  // カードの選択状態を切り替え
                    }
                }
            } else {
                repeat(5) {
                    CardImage("blank", false) {}  // 手札が非表示の場合、空のカードを表示
                }
            }
        }
        Spacer(modifier = Modifier.height(80.dp))  // 手札とボタンの間にスペースを追加
        if (isHandRevealed) {
            Text(
                text = "手札の役: $handRank",  // 手札の役を表示
                style = MaterialTheme.typography.headlineLarge.copy(fontSize = 30.sp)  // テキストのスタイルとサイズを設定
            )
            Spacer(modifier = Modifier.height(80.dp))  // テキストとボタンの間にスペースを追加
            Row {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            val selectedCardIndices = selectedCards.mapIndexedNotNull { index, isSelected ->
                                if (isSelected) index else null  // 選択されたカードのインデックスを取得
                            }
                            val remainingCards = hand.filterIndexed { index, _ -> !selectedCardIndices.contains(index) }  // 残りのカードを取得
                            val pyList = PyObject.fromJava(remainingCards.toTypedArray())  // 残りのカードをPythonリストに変換
                            val resultString = pokerGame.callAttr("replace_cards", pyList).toString()  // Python関数を呼び出して結果を取得
                            val result = JSONObject(resultString)  // 結果をJSONオブジェクトに変換
                            val newCardList = result.getJSONArray("hand").let { jsonArray ->
                                List(jsonArray.length()) { jsonArray.getString(it) }  // 新しい手札のカードリストを取得
                            }

                            handRank = result.getString("rank")  // 新しい手札の役を取得
                            handRankValue = result.getInt("rank_value")  // 新しい手札の役の値を取得
                            handlevel = result.getInt("hand_level")  // 新しい手札のレベルを取得
                            hand.clear()  // 手札をクリア
                            hand.addAll(newCardList)  // 新しい手札を追加
                            selectedCards.clear()  // 選択されたカードをクリア
                            selectedCards.addAll(List(newCardList.size) { false })  // 選択されたカードを初期化

                            currentPlayer.hand = newCardList  // 現在のプレイヤーの手札を更新
                            currentPlayer.handRank = handRank  // 現在のプレイヤーの手札の役を更新
                            currentPlayer.handRankValue = handRankValue  // 現在のプレイヤーの手札の役の値を更新
                            currentPlayer.handlevel = handlevel  // 現在のプレイヤーの手札のレベルを更新

                            isReplaceEnabled = false  // カードを入れ替えるボタンを無効にする
                        }
                    },
                    enabled = isReplaceEnabled && selectedCards.any { it }  // ボタンの有効/無効を制御
                ) {
                    Text(
                        text = "カードの交換",  // ボタンに表示するテキスト
                        style = MaterialTheme.typography.headlineLarge.copy(fontSize = 20.sp)  // テキストのスタイルとサイズを設定
                    )
                }
                Spacer(modifier = Modifier.width(20.dp))  // ボタン間にスペースを追加
                Button(onClick = {
                    if (currentPlayerIndex < players.size - 1) {
                        currentPlayerIndex++  // 次のプレイヤーに移動
                    } else {
                        onGameEnd(players)  // ゲーム終了時に呼び出す
                    }
                }) {
                    Text(
                        text = "終了",  // ボタンに表示するテキスト
                        style = MaterialTheme.typography.headlineLarge.copy(fontSize = 20.sp)  // テキストのスタイルとサイズを設定
                    )
                }
            }
        } else {
            Spacer(modifier = Modifier.height(45.dp))  // ボタンの上にスペースを追加

            Button(onClick = { isHandRevealed = true },
                modifier = Modifier
                    .width(200.dp)  // 幅を200dpに設定
                    .height(60.dp)  // 高さを60dpに設定
            ) {
                Text(
                    text = "OK",  // ボタンに表示するテキスト
                    style = MaterialTheme.typography.headlineLarge.copy(fontSize = 30.sp)
                )
            }
        }
    }
}

// ランキング画面
@Composable
fun RankingScreen(players: List<Player>, onRestart: () -> Unit) {
    // プレイヤーをhandRankValueとhandlevelの順にソートし、handRankValueが1のプレイヤーを最後に移動
    val sortedPlayers = players.sortedWith(compareByDescending<Player> { it.handRankValue }
        .thenByDescending { it.handlevel })
        .partition { it.handRankValue != 1 }
        .let { (nonLowRank, lowRank) -> nonLowRank + lowRank }

    Column(
        modifier = Modifier
            .fillMaxSize()  // 画面全体を埋める
            .padding(20.dp),  // 外側の余白を設定
        horizontalAlignment = Alignment.CenterHorizontally,  // 水平方向に中央揃え
        verticalArrangement = Arrangement.Top  // 垂直方向に上揃え
    ) {
        Spacer(modifier = Modifier.height(40.dp))  // 上部にスペースを追加
        Text(
            text = "ランキング",  // 表示するテキスト
            style = MaterialTheme.typography.headlineLarge.copy(fontSize = 40.sp)  // テキストのスタイルとサイズを設定
        )
        Spacer(modifier = Modifier.height(60.dp))  // テキストとリストの間にスペースを追加

        var currentRank = 1  // 現在の順位を初期化
        var rankOffset = 0  // ランクオフセットを初期化
        var previousHandRankValue = sortedPlayers.firstOrNull()?.handRankValue ?: 0  // 前のプレイヤーのhandRankValueを初期化
        sortedPlayers.forEachIndexed { index, player ->
            if (player.handRankValue != 1) {
                currentRank = index + 1 - rankOffset  // handRankValueが1でない場合、順位を更新
                previousHandRankValue = player.handRankValue  // 前のプレイヤーのhandRankValueを更新
            } else {
                currentRank = sortedPlayers.count { it.handRankValue != 1 } + 1  // handRankValueが1の場合、最下位に設定
                rankOffset++  // ランクオフセットを増加
            }

            Text(
                text = "${currentRank}位: ${player.name} - ${player.handRank}",  // プレイヤーの順位と名前、役を表示
                style = MaterialTheme.typography.headlineLarge.copy(fontSize = 20.sp)  // テキストのスタイルとサイズを設定
            )
            Spacer(modifier = Modifier.height(5.dp))  // テキストとカードの間にスペースを追加
            Row(
                modifier = Modifier.fillMaxWidth(),  // 横幅を画面全体に設定
                horizontalArrangement = Arrangement.Center  // 水平方向に中央揃え
            ) {
                player.hand.forEach { card ->
                    CardImage(card, false) {}  // プレイヤーの手札を表示
                }
            }
            Spacer(modifier = Modifier.height(20.dp))  // 各プレイヤーの間にスペースを追加
        }

        Spacer(modifier = Modifier.height(30.dp))  // リストとボタンの間にスペースを追加
        Button(onClick = onRestart,
            modifier = Modifier
                .width(200.dp)  // 幅を200dpに設定
                .height(60.dp)  // 高さを60dpに設定
        ) {
            Text(
                text = "タイトルに戻る",  // ボタンに表示するテキスト
                style = MaterialTheme.typography.headlineLarge.copy(fontSize = 30.sp)  // テキストのスタイルとサイズを設定
            )
        }
    }
}

// カードの画像を表示するComposable関数
@Composable
fun CardImage(cardName: String, isSelected: Boolean, onClick: () -> Unit) {
    val context = LocalContext.current  // 現在のコンテキストを取得
    val cardResId = getDrawableResourceByName(context, cardName)  // カードのリソースIDを取得

    Image(
        painter = painterResource(id = cardResId),  // 画像リソースを設定
        contentDescription = cardName,  // 画像の内容説明
        modifier = Modifier
            .size(75.dp)  // 画像のサイズを設定
            .padding(1.dp)  // 画像の内側の余白を設定
            .border(2.dp, if (isSelected) Color.Red else Color.Transparent)  // 選択状態に応じて枠線を変更
            .clickable { onClick() }  // クリックイベントを設定
    )
}

// DrawableリソースIDを名前で取得するヘルパー関数
@Composable
fun getDrawableResourceByName(context: Context, name: String): Int {
    return context.resources.getIdentifier(name, "drawable", context.packageName)
}

// 背景設定
@Composable
fun Background(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.backimage01),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        content()
    }
}