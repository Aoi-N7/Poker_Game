import random

# グローバル変数としてデッキを定義
suit = ['c', 'd', 'h', 's']  # c:クラブ, d:ダイヤ, h:ハート, s:スペード
num = ['2', '3', '4', '5', '6', '7', '8', '9', 'x', 'j', 'q', 'k', 'a']
deck = [[s, n] for s in suit for n in num]

# デッキのシャッフル
def shuffle_deck():
    global deck
    deck = [[s, n] for s in suit for n in num]  # デッキを再生成
    random.shuffle(deck)

# カードを引く
def draw_hand(num_cards):
    # 指定された枚数のカードを引く
    return [deck.pop() for _ in range(num_cards)]

# 役判定関数
def judge_hand(hand):
    n = [card[1] for card in hand]  # 数字部分を取得
    s = [card[0] for card in hand]   # 記号部分を取得

    # 数字を数値に変換
    num_values = {'0': 0, '1': 0, '2': 2, '3': 3, '4': 4, '5': 5, '6': 6, '7': 7, '8': 8, '9': 9, 'x': 10, 'j': 11, 'q': 12, 'k': 13, 'a': 14}
    n_count = {number: n.count(number) for number in n}

    flush = (len(set(s)) == 1)  # フラッシュ判定
    sorted_n = sorted(num_values[number] for number in n)
    straight = all(sorted_n[i] - sorted_n[i - 1] == 1 for i in range(1, 5))  # ストレート判定
    count = sorted(list(n_count.values()), reverse=True)

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

# 役の強さを数値化する関数
def rank_n(rank):
    rank_dict = {
        "ロイヤルストレートフラッシュ": 10,
        "ストレートフラッシュ": 9,
        "フォーカード": 8,
        "フルハウス": 7,
        "フラッシュ": 6,
        "ストレート": 5,
        "スリーカード": 4,
        "ツーペア": 3,
        "ワンペア": 2,
        "ハイカード": 1
    }
    return rank_dict[rank]

def hand_l(hand, rank):
    n = [card[1] for card in hand]  # 数字部分を取得

    num_values = {'0': 0, '1': 0, '2': 2, '3': 3, '4': 4, '5': 5, '6': 6, '7': 7, '8': 8, '9': 9, 'x': 10, 'j': 11, 'q': 12, 'k': 13, 'a': 14}
    n_values = [num_values[number] for number in n]

    if rank in ["ストレート", "ストレートフラッシュ"]:
        return sum(n_values)
    elif rank in ["フォーカード", "スリーカード", "ツーペア", "ワンペア"]:
        n_count = {number: n.count(number) for number in n}
        return sum(num_values[number] * count for number, count in n_count.items() if count > 1)
    else:
        return 0



# JSON形式で役とカードを取得する関数
def get_hand_with_rank():
    hand = draw_hand(5)
    rank = judge_hand(hand)
    hand_Rank_Value = rank_n(rank)
    hand_level = hand_l(hand, rank)

    print("hand:", hand)  # デバッグ: 手札を出力
    print("Rank:", rank)  # デバッグ: 役を出力
    print("Value:", hand_Rank_Value)  # デバッグ: 役の数値を出力
    print("level", hand_level)  # デバッグ: 役のレベル出力

    return {
        "hand": [f"{card[0]}{card[1]}" for card in hand],
        "rank": str(rank),
        "rank_value": hand_Rank_Value,
        "hand_level": hand_level
    }

# 残りのカードを受け取り、新しいカードを引いて手札を5枚に補充する関数
def replace_cards(remaining_cards):
    global deck

    # remaining_cardsがPythonのリストとして扱えるように変換
    remaining_cards = list(remaining_cards)

    # 新しいカードを引くためにdraw_hand関数を使用
    num_new_cards = 5 - len(remaining_cards)
    new_cards = draw_hand(num_new_cards)
    new_hand = remaining_cards + new_cards
    rank = judge_hand(new_hand)
    hand_Rank_Value = rank_n(rank)
    hand_level = hand_l(new_hand, rank)

    print("New hand:", new_hand)  # デバッグ: 手札を出力
    print("New Rank:", rank)  # デバッグ: 役を出力
    print("New Value:", hand_Rank_Value)  # デバッグ: 役の数値を出力
    print("New level:", hand_level)  # デバッグ: 役のレベル出力

    return {
        "hand": [f"{card[0]}{card[1]}" for card in new_hand],
        "rank": str(rank),
        "rank_value": hand_Rank_Value,
        "hand_level": hand_level
    }
