package telegram_services;

/**
 * Created by kuteynikov on 07.07.2017.
 */
public enum  BotMessages {
    MAIN_MENU("Главное меню"),
    SELECT_SUBSCRIPTION("Выберите УСЛУГУ:"),
    CHECK_SUBSCRIPTION("Ваша подписка истекает: "),
    FAQ("*О «ПРОЕКТЕ»*\n" +
            "*1 Что представляет собой «New Wave»?*\n" +
            "_«New Wave» предоставляет сигналы на покупку и продажу криптовалют на возмездной основе для торговли на биржах, а так же для обмена криптовалют._\n" +
            "*2 Для чего создавался проект «New Wave»?*\n" +
            "_Проект «New Wave» создавался для того, чтобы дать возможность зарабатывать на криптовалютном рынке людям не имеющим подготовки трейдера и опыта торгов на биржах или форекс, а так же для людей имеющих опыт торговли на бирже и форексе, но желающим расширить зону своих интересов._\n" +
            "*3 Чем «New Wave» отличается от других подобных ботов по продаже сигналов?*\n" +
            "_«New Wave» предлагает не только сигналы на покупку той или иной криптовалюты, но и потенциальные уровни продажи, что значительно повышает простоту заработка на криптовалютном рынке._\n" +
            "*4 Значит можно не о чем не думать и просто зарабатывать много?*\n" +
            "_Не следует забывать о рисках, необходимо деверсифицировать риски, вкладывая средства в портфель из разных валют на разных биржах._\n" +
            "*НАЧАЛО РАБОТЫ*\n" +
            "*5 Как начать работать с «New Wave»?*\n" +
            "_Необходимо завести аккаунты на нескольких биржах криптовалют, таких как, например, poloniex.com и bittrex.com, внести номер своего кошелька в платежной системе advcash, в соответствующем разделе настроек, произвести оплату подписки на выбранный Вами срок (1, 2 или 3 месяца а так же VIP-подписка) через платежную систему advcash в долларах, подождать, пока Ваш платеж будет зарегистрирован в системе и получать информацию, для Ваших сделок._\n" +
            "*6 В каком виде приходят сигналы?*\n" +
            "_Сигналы содержат информацию о том, какую криптовалюту купить, на каких биржах возможна покупка, по какой цене купить, уровни продажи._\n" +
            "*7 Каком примерный список бирж, на которых нужно зарегистрироваться?*\n" +
            "_https://poloniex.com/_\n" +
            "_https://bittrex.com/_\n" +
            "_https://exmo.me/_\n" +
            "_https://btc-e.nz/_\n" +
            "*8 Обязательно вводить номер кошелька advcash?*\n" +
            "_Да, по этой информации мы отслеживаем от кого приходит оплата подписки, а так же начисляем реферальное вознаграждение._\n" +
            "*ПРО ОПЛАТУ*\n" +
            "*9 Возможно ли исопльзовать для оплаты другие платежные системы или другие валюты?*\n" +
            "_Оплата принимается только в долларах и через систему advcash._\n" +
            "*10 Сколько времени зачисляется платеж, через какое количество времени я начну получать сигналы?*\n" +
            "_Обычно оплата происходит сразу, однако, возможны отклонения до 24 часов_\n" +
            "*11 Прошло более 24 часов я не начал получать сигналы по подписке.*\n" +
            "_Обратитесь, пожалуйста в тех поддержку_\n" +
            "*ПРО РЕФЕРАЛЬНУЮ СИСТЕМУ*\n" +
            "*12 Сколько линий в реферально системе?*\n" +
            "_В реферально системе 3 уровня, все 3 уровня доступны Вам сразу._\n" +
            "*13 Каково вознаграждения за привлеченных рефералов по линиям?*\n" +
            "_1-линия – 10%, 2-линия – 5%, 3-линия – 3%, кроме того существует бонус за количество привлеченных рефералов, первой линии, оплативших подписку (за 10 человек  - 1000 $)_\n" +
            "*14 Я вижу не всех своих рефералов.*\n" +
            "_Обратитесь, пожалуйста, в техподдержку._\n" +
            "*15 Я не вижу своего рефовода*\n" +
            "_Обратитесь, пожалуйста, в техподдержку._\n" +
            "*16 Можно ли поменять рефовода?*\n" +
            "_Нет, относитесь ответственно к выбору рефовода._\n" +
            "*Общие вопросы*\n" +
            "*17 Выгоднее покупать сигналы на бОльший срок?*\n" +
            "_Да,  пакет 600$мес*12 мес = 7200$ в год, пакет 800$ 2 мес * 12 мес = 4800$ в год, пакет 1000$ 3 мес = 4000$ в год_"),
    HOW_TO_CHANGE_CURRENCY("Если Вам требуется обмен криптовалюты, вы можете обратиться к @ich333,@Sorokrs, или @Jahmaiker"),
    SUPPORT("Если Вам требуется техподдержка, Вы можете обратиться к @ich333"),
    SHORT_DESCRIPTION("Информация"),
    GENERAL_DESCRIPTION("По данным coinmarketcap.com за год с июня 2016 по июнь 2017 года, рост курса криптовалют имел взрывной характер и составил 10617% для NEM, 4568 % для Ripple, 2308% для Etherium.\n" +
            "Столь поражающие цифры заставляют людей задумываться о том, чтобы начать инвестировать в криптовалюты. Однако, человеку, не имеющему опыта работы на криптовалютном рынке или на рынке-форекс сложно сориентироваться в огромном разнообразии валют. С чего начать, как сделать правильный выбор, в какие криптовалюты инвестировать накопленные средства, чтобы их преумножить и не прогадать?\n" +
            "Мы, сервис по предоставлению торговых сигналов «New Wave», предлагаем Вам поймать волну и заработать на росте рынка криптовалют. Команда аналитиков проекта ежедневно мониторит огромное количество проектов, чтобы предоставить Вам информацию о самых лучших из них, которые принесут наибольшую прибыль.\n" +
            "Наши сигналы обладают высоким качеством и содержат информацию о том, какую криптовалюту купить, на какой бирже, по какой цене, а также информацию о том по какой цене продать, чтобы извлечь максимальную прибыль. Кроме того, мы предоставляем услуги по аудиту Вашего портфеля криптовалют с целью улучшения показателей доходности и надежности.\n" +
            "В New Wave мы ставим своей целью улучшение качества жизни клиентов, чтобы и вы смогли поймать волну и войти в эпоху цифровой экономики, где термин «криптовалюта» не будет пугающим, а будет служить средством заработка для многих людей."),
    ONE_MONTH("Стоимость подписки на 1 месяц = 6р" +
            "\nНажмите \"Оплатить\", чтобы перейти к оплате  на сайте Advcash"),
    TWO_MONTH("Стоимость подписки на 2 месяца = 7р"
            +"\nНажмите \"Оплатить\", чтобы перейти к оплате  на сайте Advcash"),
    THREE_MONTH("Стоимость подписки на 3 месяца = 8р"
            +"\nНажмите \"Оплатить\", чтобы перейти к оплате  на сайте Advcash"),
    UNLIMIT_SUBSCRIPTION("Стоимость безлимитной подписки = 10р"
            +"\nНажмите \"Оплатить\", чтобы перейти к оплате  на сайте Advcash"),
    DEFAULT("Я пока не знаю что на это ответить"),
    SETTINGS_MENU("Меню настроек");
    private String textMessage;

    BotMessages(String textMessage) {
        this.textMessage = textMessage;
    }

    public String getText(){
        return this.textMessage;
    }
}
