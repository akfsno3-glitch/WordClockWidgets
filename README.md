# WordClockWidgets
Часы-виджеты с отображением времени словами на русском языке

## Быстрый старт

1. Установите JDK 17
2. Установите Android SDK и Android SDK Command-line Tools
3. Создайте `local.properties`:

```properties
sdk.dir=/usr/lib/android-sdk
```

4. Сборка:

```bash
./gradlew clean assembleDebug --no-daemon
```

APK будет в `app/build/outputs/apk/debug/app-debug.apk`.

## Тестирование

### Веб-тестер конструктора
Для тестирования логики перемещения элементов запустите:

```bash
cd /workspaces/WordClockWidgets
python3 -m http.server 8000
```

Затем откройте в браузере: `http://localhost:8000/test_constructor.html`

Тестер включает:
- Перетаскивание элементов мышью
- Джойстик для точного позиционирования
- Сетку 6x2 ячеек (как в реальном конструкторе)
- Индикаторы ошибок (красные точки) для элементов вне границ
- Отображение координат в реальном времени

### Установка на устройство
1. Подключите Android устройство с включенной отладкой USB
2. Установите APK:

```bash
./gradlew installDebug
```

Или вручную:
```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

## CI (GitHub Actions)

В проекте добавлен workflow: `.github/workflows/android-ci.yml`
- запускается на `push` и `pull_request` в `main`
- собирает `assembleDebug`
- загружает артефакт `app-debug-apk`.

