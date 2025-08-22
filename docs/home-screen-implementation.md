# ホーム画面実装 - GitHub Issue #6

## 概要

アプリ側のメイン（ホーム）画面を実装し、キャラクター選択機能とアラームカスタマイズ機能を追加しました。

## 実装した機能

### 1. キャラクター選択機能

#### PersonaRepository の拡張
- `PersonaRepository` インターフェースに `list()` メソッドを追加
- `PersonaRepositoryImpl` でハードコーディングされたキャラクター情報を実装

#### 実装されたキャラクター（4種類）

| キャラクター | トーン | エネルギー | 特徴 |
|------------|--------|------------|------|
| 元気なアシスタント | Cheerful | High | 明るく前向き、エネルギッシュ |
| 優しいアシスタント | Friendly | Medium | 穏やかで思いやりがある |
| 厳格なアシスタント | Strict | High | 規律正しく時間を重視 |
| クールなアシスタント | Deadpan | Low | 冷静で論理的、効率重視 |

### 2. アラームカスタマイズ機能

#### 次のアラーム管理
- 次のアラーム時刻の表示
- アラームの有効/無効切り替え
- アラーム時刻の表示フォーマット（HH:mm）

#### カスタム時間設定
- タイムピッカーを使用した直感的な時刻選択
- 一回限りのアラーム設定オプション
- カスタム時間のリセット機能

#### アラーム制御
- 次のアラームをスキップする機能
- デフォルト設定への復帰機能

## UI実装詳細

### ホーム画面の構成

```
HomeScreen
├── AlarmStatusCard          # アラーム状態表示
├── PersonaSelectionSection  # キャラクター選択
├── AlarmCustomSection       # アラーム設定
└── TimePickerDialog        # 時刻選択ダイアログ
```

### コンポーネント詳細

#### 1. AlarmStatusCard
- **機能**: 次のアラーム時刻と有効/無効状態を表示
- **UI要素**: 
  - 大きな時刻表示（headlineLarge）
  - 有効/無効切り替えスイッチ
  - プライマリコンテナーカラーでの強調表示

#### 2. PersonaSelectionSection
- **機能**: 利用可能なキャラクターの表示と選択
- **UI要素**:
  - 横スクロール可能なキャラクターカード
  - 選択状態の視覚的フィードバック
  - ローディング状態の表示

#### 3. PersonaCard
- **機能**: 個別キャラクターの情報表示
- **UI要素**:
  - キャラクター名
  - トーンの日本語表示
  - 選択時の色変更（プライマリカラー）

#### 4. AlarmCustomSection
- **機能**: アラーム設定のカスタマイズ
- **UI要素**:
  - カスタム時間設定ボタン
  - 一回限りアラームの切り替えスイッチ
  - スキップ・リセットボタン

#### 5. TimePickerDialog
- **機能**: 時刻選択用のモーダルダイアログ
- **UI要素**:
  - Material 3 TimePicker
  - キャンセル・設定ボタン

## データフロー

### 状態管理（HomeUiState）

```kotlin
data class HomeUiState(
    val nextAlarm: String = "--:--",           // 次のアラーム時刻
    val enabled: Boolean = true,               // アラーム有効状態
    val history: List<ConversationTurn> = emptyList(), // 会話履歴
    val availablePersonas: List<AssistantPersona> = emptyList(), // 利用可能キャラクター
    val selectedPersona: AssistantPersona? = null,     // 選択中キャラクター
    val customAlarmTime: LocalTime? = null,    // カスタム時刻
    val isOneTimeAlarm: Boolean = false,       // 一回限りフラグ
    val isLoading: Boolean = false             // ローディング状態
)
```

### ViewModelの主要メソッド

| メソッド | 機能 | 説明 |
|----------|------|------|
| `loadPersonas()` | キャラクター読み込み | Repository からキャラクター一覧を取得 |
| `loadCurrentPersona()` | 現在のキャラクター取得 | ユーザーが選択中のキャラクターを取得 |
| `onSelectPersona()` | キャラクター選択 | 新しいキャラクターを選択・保存 |
| `onSetCustomAlarmTime()` | カスタム時刻設定 | 一回限りのアラーム時刻を設定 |
| `onToggleOneTimeAlarm()` | 一回限りフラグ切り替え | 一回限りアラームの有効/無効 |
| `onSkipNextAlarm()` | 次回アラームスキップ | 次のアラームを無効化 |
| `onResetToDefaultAlarm()` | デフォルト復帰 | カスタム設定をリセット |

## アーキテクチャ

### レイヤー構成

```
app/
├── ui/screen/home/
│   ├── HomeScreen.kt        # UI コンポーネント
│   └── HomeViewModel.kt     # ビジネスロジック

domain/
├── repository.kt            # PersonaRepository インターフェース
└── models/persona.kt        # AssistantPersona データモデル

infra/
├── repository/
│   └── PersonaRepositoryImpl.kt  # Repository 実装
└── ApplicationBindsModule.kt     # DI 設定
```

### 依存関係

```
HomeViewModel
├── PersonaRepository (注入)
└── LlmChatGateway (注入)

PersonaRepositoryImpl
└── ハードコーディングされたキャラクターデータ
```

## 技術的詳細

### 使用技術
- **UI**: Jetpack Compose + Material 3
- **状態管理**: StateFlow + collectAsStateWithLifecycle
- **依存性注入**: Hilt
- **時刻処理**: java.time.LocalTime
- **非同期処理**: Kotlin Coroutines

### デザインパターン
- **Repository パターン**: データアクセスの抽象化
- **MVVM パターン**: UI とビジネスロジックの分離
- **State Hoisting**: 状態の適切な管理

## 将来の拡張性

### サーバー連携への対応
- `PersonaRepositoryImpl` の実装を変更するだけで API 連携が可能
- インターフェースは変更不要
- キャラクター情報の動的取得に対応

### 機能拡張の可能性
- キャラクターの詳細設定（声の設定など）
- アラームパターンの複雑化
- 複数アラームの管理
- キャラクターのカスタマイズ

## テスト観点

### 単体テスト対象
- `HomeViewModel` のビジネスロジック
- `PersonaRepositoryImpl` のデータ取得ロジック
- 状態遷移の検証

### UI テスト対象
- キャラクター選択の動作
- タイムピッカーの操作
- スイッチ・ボタンの動作

## 既知の制限事項

1. **ハードコーディングデータ**: キャラクター情報は現在固定値
2. **簡易ダイアログ**: TimePickerDialog は簡易実装（AlertDialog への変更推奨）
3. **永続化未実装**: 選択状態はアプリ再起動で失われる
4. **エラーハンドリング**: 基本的なエラーハンドリングのみ実装

## 関連ファイル

### 新規作成
- `infra/src/main/java/io/github/arashiyama11/a_larm/infra/repository/PersonaRepositoryImpl.kt`

### 主要変更
- `app/src/main/java/io/github/arashiyama11/a_larm/ui/screen/home/HomeScreen.kt`
- `app/src/main/java/io/github/arashiyama11/a_larm/ui/screen/home/HomeViewModel.kt`
- `domain/src/main/java/io/github/arashiyama11/a_larm/domain/repository.kt`
- `infra/src/main/java/io/github/arashiyama11/a_larm/infra/ApplicationBindsModule.kt`

---

