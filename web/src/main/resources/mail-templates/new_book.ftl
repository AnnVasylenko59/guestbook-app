<#ftl output_format="HTML">
<!DOCTYPE html>
<html lang="uk">
<head>
    <meta charset="UTF-8">
    <title>Нова книга в каталозі</title>
</head>
<body style="margin:0; padding:0; background:#f4f1ec; font-family:Arial, Helvetica, sans-serif; color:#2b2b2b;">

<table role="presentation" width="100%" cellpadding="0" cellspacing="0" style="background:#f4f1ec; padding:24px 0;">
    <tr>
        <td align="center">
            <table role="presentation" width="560" cellpadding="0" cellspacing="0"
                   style="background:#ffffff; border-radius:8px; padding:32px; max-width:560px;">
                <tr>
                    <td>
                        <img src="https://raw.githubusercontent.com/VladPiatachenko/MPF_Labs/lab7-freemaker/web/src/main/resources/static/img/logo.png"
                             style="width:150px; margin-bottom:20px;" alt="BookApp">

                        <h1 style="margin:0 0 16px; font-size:24px; color:#1f3a5f;">Нова книга в каталозі</h1>

                        <#-- Панель повідомлення -->
                        <div style="background:#eef3f9; border:1px solid #c5d3e4; border-radius:6px; padding:16px 20px; margin-bottom:16px;">
                            <p style="margin:0 0 8px;"><b>Назва:</b> ${title}</p>
                            <p style="margin:0 0 8px;"><b>Автор:</b> ${author}</p>
                            <p style="margin:0 0 8px;"><b>Рік видання:</b> ${year?c}</p>
                            <p style="margin:0;"><b>Додано:</b> ${added}</p>
                        </div>

                        <#if year < 2000>
                            <p style="color:#a36b00"><b>Раритетне видання!</b></p>
                        </#if>

                        <hr style="border:none; border-top:1px solid #e3ded5; margin:24px 0 12px;">
                        <p style="margin:0; font-size:12px; color:#888888;">BookApp © ${.now?string("yyyy")}</p>
                    </td>
                </tr>
            </table>
        </td>
    </tr>
</table>

</body>
</html>
