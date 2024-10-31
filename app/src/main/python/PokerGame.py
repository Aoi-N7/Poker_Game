import random

# グローバル変数としてデッキを定義
suit = ['c', 'd', 'h', 's']  # c:クラブ, d:ダイヤ, h:ハート, s:スペード
num = ['2', '3', '4', '5', '6', '7', '8', '9', '10', 'j', 'q', 'k', 'a']
deck = [[s, n] for s in suit for n in num]

# デッキのシャッフル
def shuffle_deck():
    global deck
    deck = [[s, n] for s in suit for n in num]  # デッキを再生成
    random.shuffle(deck)

# カードを引く
def draw_hand():
    # 5枚のカードを引く
    return [deck.pop() for x in range(5)]

# 役判定関数
def judge_hand(hand):
    n = [card[1] for card in hand]    # 数字
    s = [card[0] for card in hand]    # 記号

    # 数字を数値に変換
    num_values = {'2': 2, '3': 3, '4': 4, '5': 5, '6': 6, '7': 7, '8': 8, '9': 9, '10': 10, 'j': 11, 'q': 12, 'k': 13, 'a': 14}
    n_count = {number: n.count(number) for number in n}

    flush = (len(set(s)) == 1)  # フラッシュ判定
    sorted_n = sorted(num_values[number] for number in n)
    straight = all(sorted_n[i] - sorted_n[i - 1] == 1 for i in range(1, 5))  # ストレート判定
    count = sorted(n_count.values(), reverse=True)

    if straight and flush and sorted_n[-1] == 14:
        return "ロイヤルストレートフラッシュ"
    elif straight and flush:
        return "ストレートフラッシュ"
    elif count == [4, 1]:
        return "フォーカード"
    elif count == [3, 2]:
        return "フルハウス"
    elif flush:
        return "フラッシュ"
    elif straight:
        return "ストレート"
    elif count == [3, 1, 1]:
        return "スリーカード"
    elif count == [2, 2, 1]:
        return "ツーペア"
    elif count == [2, 1, 1, 1]:
        return "ワンペア"
    else:
        return "ハイカード"

# JSON形式で役とカードを取得する関数
def get_hand_with_rank():
    hand = draw_hand()
    rank = judge_hand(hand)

    print(hand)  # デバッグ: 手札を出力
    print(rank)  # デバッグ: 役を出力

    return {
        "hand": [f"{card[0]}{card[1]}" for card in hand],  # 例: ['c2', 'd5', 'h7', 's9', 'cq']
        "rank": str(rank)
    }
