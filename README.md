
# 🔒 USDT Checker Bot

**Telegram-бот для проверки крипто-кошельков на связь с заблокированными адресами Garantex**  
Специализированный инструмент для проверки принадлежности кошельков к санкционированной бирже Garantex.

[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.0-purple.svg)](https://kotlinlang.org/)
[![Telegram](https://img.shields.io/badge/Telegram-Bot%20API-blue.svg)](https://core.telegram.org/bots/api)

## 🚀 Быстрый доступ
Просто перейдите в Telegram и начните проверку:  
👉 [@usdt_checker_bot](https://t.me/usdt_checker_bot) 👈


## 🌟 Возможности

- 🕵️ **Целевая проверка**  
  Сверка адресов с актуальной базой заблокированных кошельков Garantex.
- 📁 **Поддержка форматов**  
  Работа с одиночными адресами, `.txt` (каждый адрес с новой строки) и `.csv` (колонка `From`) файлами.
- 📆 **Актуальная база**  
  База содержит только адреса, связанные с Garantex (последнее обновление: 18.03.2025).
- 🔒 **Конфиденциальность**  
  Данные проверок не сохраняются.


## 📂 Источник данных
База адресов формируется на основе:  
✅ **Официальных списков Garantex**  
✅ [**Sunscrypt USDT чекер**](https://tools.sunscrypt.ru/checker/)

## 📋 Пример использования

**Отправьте боту:**

1. **Текст:**
```text
0x742d35Cc6634C0532925a3b844Bc454e4438f44e
0x8F47F8AC...
```
2.  **Файл:**  
    `wallets.csv`  (с колонкой  `From`)

**Получите отчет:**

📊 **Результаты проверки**

🔢 Проверено адресов: 5

🎯 Совпадений найдено: 5

📦 Всего в базе: 3530

Совпавшие адреса:

• TJGBCviPKtaTdY1Nk7S7TGHVBNjRAdFM8f

• TAabrsoK8Y2ANB8SJsEDbCus6Ha59fxGhc

• TGGgCca1NoCbSXt1dNtKjXmVqqXnwrYkaK

• TS6HNGuhFSrUZ4WGyrCtMfwgJe62BXvSEG

• TY8nvVKhn2ExTbphJ49RW17ULMKu9hm17n

----------

## ⚠️ Важно

-   База содержит  **только адреса**, связанные с Garantex

-   Для проверки используются последние официальные данные

-   При отправке файла CSV убедитесь в наличии колонки  `From`


----------

## 📄 Лицензия

**WTFPL**  
_Инструмент для проверки исторических связей. Не является финансовым советом._