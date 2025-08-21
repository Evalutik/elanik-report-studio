<#macro showStatus text>
    <#if (text?has_content) && (text?length >= 4)>
        <#assign prefix = text?substring(0,4)>
        <#assign rest = text?substring(4)>
        <#if prefix == "(✓) ">
            <strong style="color:green;">✓ </strong>${rest}
        <#elseif prefix == "(!✓)">
            <strong style="color:gold;">✓ </strong>${rest}
        <#elseif prefix == "(✕) ">
            <strong style="color:red;">✕ </strong>${rest}
        <#else>
            ${text}
        </#if>
    <#else>
        ${text}
    </#if>
</#macro>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <style type="text/css">
        @page { size: A4; margin: 15mm; }
        *{
            margin: 0;
            padding: 0;
        }
        body {
            font-family: 'Roboto-Regular', 'DejaVu Sans', sans-serif;
            font-size: 0.8rem;
            color: #111;
            margin: 3rem 4rem;
        }
        img{
            height: 25px;
            margin-right: 10px;
        }

        h1 {
            font-size: 1.3rem;
            line-height: 1.2;
        }
        p {
            font-size: 0.8rem;
            line-height: 1.15;
        }
        .header{
            margin-bottom: 1.5rem;
        }
        .header-col{
            display: inline-block;
            vertical-align: middle;
            line-height: 1.2;
        }
        .header p{
            font-size: 0.9rem;
        }
        .meta p{
            font-size: 0.9rem;
        }

        .measurement {
            margin-bottom: 2.5rem;
            page-break-inside: avoid;
        }
        .measurement p{font-size: 0.9rem;}
        .row{
            line-height: 1.15;
        }
        .row div{
            display: inline-block;
        }
        .row div.subtext{
            float: right;
        }
        .measurement .info{
            margin-bottom: 1rem;
        }

        .underline{
            text-decoration: underline;
            display: inline-block;
            width: 10rem;
            border-bottom: 1px solid black;
        }


        table { width: 100%; border-collapse: collapse; margin-top: 6px; }
        th, td {
            border: 1px solid #ddd;
            padding: 5px;
            text-align: left;
            font-size: 0.75rem;
            line-height: 1.18;
        }
        th { background: #f0f0f0; font-weight: bold; }
        .nowrap { white-space: nowrap; }
        /* avoid breaking a row across pages */
        tr { page-break-inside: avoid; }
        /* force new table on new page if needed */


        @media print {
            body {
                margin: 0; /* Let @page handle margins in PDF/print mode */
            }
        }
    </style>
    <title>Отчёт по результатам измерений</title>
</head>
<body>
<div class="header">
    <div class="header-col">
        <img src="${logoBase64?no_esc}" alt="Логотип Компании"/>
    </div>
    <div class="header-col">
        <h1>Отчёт по результатам измерений</h1>
    </div>
    <p>
        Лазерно-искровой эмиссионный спектрометр "ЭЛАНИК"
    </p>
    <p>
        <#if serial?? && serial?has_content>
            Серийный №: ${serial}
        </#if>
    </p>
</div>

<#list measurements as m>
<div class="measurement">
    <div class="info">
        <p><strong>Измерение № ${m.id}</strong> от ${m.dateTime}</p>
        <p>Количество точек измерения: ${m.pointsNum}</p>
        <p>
            <#if m.comment?? && m.comment?has_content>
                Комментарий пользователя: ${m.comment}
            </#if>
        </p>
    </div>

    <div class="table-data">
        <p>
            <#if m.ce?? && m.ce?has_content>
                Углеродный эквивалент (CE): ${m.ce}
            </#if>
        </p>
        <table>
            <thead>
            <tr>
                <th>Элемент</th>
                <th>%</th>
                <th>±</th>
                <th><@showStatus text=(m.alloyNames[0]! "Марка 1")/></th>
                <th><@showStatus text=(m.alloyNames[1]! "Марка 2")/></th>
                <th><@showStatus text=(m.alloyNames[2]! "Марка 3")/></th>
            </tr>
            </thead>
            <tbody>
            <#list m.elementsData as el>
            <tr>
                <td><strong>${el.name}</strong></td>
                <td>${(el.concentration)}</td>
                <td>${(el.deviation)}</td>
                <td><@showStatus text=(el.alloyType1! "")/></td>
                <td><@showStatus text=(el.alloyType2! "")/></td>
                <td><@showStatus text=(el.alloyType3! "")/></td>
            </tr>
            </#list>
            </tbody>
        </table>
    </div>
</div>
</#list>

<div class="meta">
    <p>Время и дата создания отчёта: ${creationDateTime}</p>
    <p style="margin-top: 1rem">Отчёт подготовлен: <span class="underline"></span> / <span class="underline"></span> .</p>
</div>

</body>
</html>
