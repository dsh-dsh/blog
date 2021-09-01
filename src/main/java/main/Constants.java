package main;

public final class Constants {

    public static final String FILE_SIZE_ERROR = "Размер файла превышает допустимый размер";
    public static final String FILE_UPLOAD_ERROR = "Не удалось загрузить файл";
    public static final String FILE_MISSING_ERROR = "Файл отсутствует";
    public static final String WRONG_CAPTCHA_CODE = "Код с картинки введён неверно";
    public static final String EMAIL_EXISTS = "Этот e-mail уже зарегистрирован";
    public static final String EMAIL_USER_NOT_FOUND = "Пользователь %s не найден";
    public static final String POST_NOT_FOUND = "Пост не найден";
    public static final String ID_POST_NOT_FOUND = "Пост с id = %d не найден";
    public static final String WRONG_IMAGE_TYPE = "Изображение должно быть jpeg или png";
    public static final String TEXT_TOO_SHORT = "Текст не задан или слишком короткий";
    public static final String TITLE_TOO_SHORT = "Заголовок не задан или слишком короткий";
    public static final String PASSWORD_TOO_SHORT = "Пароль короче 6-ти символов";
    public static final String WRONG_NAME = "Имя указано неверно";
    public static final String ID_COMMENT_NOT_FOUND = "Комментарий с id = %d не найден";
    public static final String RESTORE_EMAIL_TITLE = "Востановление пароля";
    public static final String RESTORE_EMAIL_MESSAGE = "Для востановления пароля перейдите по ссылке %s.\n" +
            "Ссылка действительна в течении 48 часов";
    public static final String WRONG_CODE_RESTORE = "Код не совпадает";
    public static final String CODE_RESTORE_EXPIRED = "Ссылка для восстановления пароля устарела.\n" +
            "<a href=\n" +
            "\\\"/auth/restore\\\">Запросить ссылку снова</a>";
    public static final String USER_NOT_FOUND = "Пользователь не найден";
    public static final String RESIZE_FILE_ERROR = "Не получилось измененить размер загруженого изображения";
    public static final String POST_DATE_ERROR = "Ошибка в дате";
    public static final String NO_POSTS_YET = "Еще нет опубликованных постов";
}
