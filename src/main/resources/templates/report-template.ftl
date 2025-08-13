<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <style type="text/css">
        @page { size: A4; margin: 24mm; }
        body { font-family: 'DejaVu Sans', sans-serif; font-size: 12px; color: #111; }
        h1 { font-size: 16px; margin-bottom: 6px; }
        .meta { font-size: 10px; color: #444; margin-bottom: 12px; }
        .measurement { margin-bottom: 18px; page-break-inside: avoid; }
        table { width: 100%; border-collapse: collapse; margin-top: 6px; }
        th, td { border: 1px solid #ddd; padding: 6px; text-align: left; font-size: 11px; }
        th { background: #f0f0f0; font-weight: bold; }
        .nowrap { white-space: nowrap; }
        /* avoid breaking a row across pages */
        tr { page-break-inside: avoid; }
        /* force new table on new page if needed */
    </style>
    <title>title Example</title>
</head>
<body>
<h1>Report</h1>
<div class="meta">
    Created: ${creationDateTime}
    <#if serial?? && serial?has_content>
        Serial: ${serial}
    </#if>
</div>

<#list measurements as m>
<div class="measurement">
    <div>
        <strong>Measurement Id:</strong> ${m.id}
        <strong>Time:</strong> ${m.dateTime}
        <strong>Points number:</strong> ${m.pointsNum}

        <#if m.baseElementName?? && m.baseElementName?has_content>
            <strong>Base Element:</strong> ${m.baseElementName}
        </#if>
        <#if m.alloyType?? && m.alloyType?has_content>
            <strong>Alloy Type:</strong> ${m.alloyType}
        </#if>
        <#if m.ce?? && m.ce?has_content>
            <strong>CE:</strong> ${m.ce}
        </#if>


        <#if m.comment?? && m.comment?has_content>
            <strong>User comment:</strong> ${m.comment}
        </#if>


    </div>

    <table>
        <thead>
        <tr>
            <th>Element</th>
            <th>%</th>
            <th>±</th>
            <th>${m.alloyNames[0]! "M1"}</th>
            <th>${m.alloyNames[1]! "M2"}</th>
            <th>${m.alloyNames[2]! "M3"}</th>
        </tr>
        </thead>
        <tbody>
        <#list m.elementsData as el>
        <tr>
            <td>${el.name}</td>
            <td>${(el.concentration)}</td>
            <td>${(el.deviation)}</td>
            <td>${el.alloyType1}</td>
            <td>${el.alloyType2}</td>
            <td>${el.alloyType3}</td>
        </tr>
        </#list>
        </tbody>
    </table>
</div>
</#list>
</body>
</html>
