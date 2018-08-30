var month_leap = [31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31];
var month_normal = [31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31];
var month_name = [
  "January",
  "February",
  "March",
  "April",
  "May",
  "June",
  "July",
  "August",
  "September",
  "October",
  "November",
  "December"
];
var month_map = {
  January: 1,
  February: 2,
  March: 3,
  April: 4,
  May: 5,
  June: 6,
  July: 7,
  August: 8,
  September: 9,
  October: 10,
  November: 11,
  December: 12
};
var daysDisplay = document.getElementById("days");
var prev = document.getElementById("prev");
var next = document.getElementById("next");
var monthElement = document.getElementById("calendar-month");
var yearElement = document.getElementById("calendar-year");
var diaryElement = document.getElementById("diary");
var date = new Date();
var year = date.getFullYear();
var month = date.getMonth();
var today = date.getDate();
var lastClass; //class of last pick
var lastElement; //element of last pick
var classToday = '"blue bluebox"';
var classFocus = "yellow yellowbox"; //no double quotation marks
var classPast = '"lightgrey"';
var classFuture = '"darkgrey"';
var classNoted = '"green greenbox"';
var start = "From";
var end = "To";
var content = "Content";

function createXmlHttpRequest() {
  if (window.ActiveXObject) {
    return new ActiveXObject("Microsoft.XMLHTTP");
  } else if (window.XMLHttpRequest) {
    return new XMLHttpRequest();
  }
}

function dateClickEvent(year, month, date, isdbclick) {
  var xhr = createXmlHttpRequest();
  xhr.onreadystatechange = function () {
    if (xhr.readyState == 4 && xhr.status == 200) {
      add_inner_html(diaryElement, xhr.responseText, isdbclick, date);
    } else if (xhr.readyState == 4) {
      alert("bad request");
    }
  };
  xhr.open("GET", "diary/" + year + "/" + month_map[month] + "/" + date, true);
  xhr.send();
}

prev.onclick = function (e) {
  e.preventDefault();
  month--;
  if (month < 0) {
    year--;
    month = 11;
  }
  refreshDate();
};

next.onclick = function (e) {
  e.preventDefault();
  month++;
  if (month > 11) {
    year++;
    month = 0;
  }
  refreshDate();
};

var dates_recorder;

function refreshDate() {
  var xhr = createXmlHttpRequest();
  xhr.onreadystatechange = function () {
    if (xhr.readyState == 4 && xhr.status == 200) {
      dates_recorder = JSON.parse(xhr.responseText);
      refresh_normal(dates_recorder);
      addEventForDates();
    } else if (xhr.readyState == 4) {
      refresh_error();
      addEventForDates();
    }
  };
  xhr.open("GET", "diary/" + year + "/" + (month + 1), true);
  xhr.send();
}

function refresh_normal(date_list) {
  var str = "";
  var totalDay = numOfDays(month, year);
  var firstDay = dateStart(month, year);
  var tclass;
  for (var i = 1; i < firstDay; i++) {
    str += "<li></li>";
  }
  for (var i = 1; i <= totalDay; i++) {
    if (i == today && year == date.getFullYear() && month == date.getMonth()) {
      tclass = " class=" + classToday;
    } else if (dates_recorder.indexOf(i) != -1) {
      tclass = " class=" + classNoted;
    } else if (
      (i < today && year == date.getFullYear() && month == date.getMonth()) ||
      year < date.getFullYear() ||
      (year == date.getFullYear() && month < date.getMonth())
    ) {
      tclass = " class=" + classPast;
    } else {
      tclass = " class=" + classFuture;
    }
    str += "<li" + tclass + ">" + i + "</li>";
  }
  daysDisplay.innerHTML = str;
  monthElement.innerHTML = month_name[month];
  yearElement.innerHTML = year;
}

function refresh_error() {
  var str = "";
  var totalDay = numOfDays(month, year);
  var firstDay = dateStart(month, year);
  var tclass;
  for (var i = 1; i < firstDay; i++) {
    str += "<li></li>";
  }
  for (var i = 1; i <= totalDay; i++) {
    if (
      (i < today && year == date.getFullYear() && month == date.getMonth()) ||
      year < date.getFullYear() ||
      (year == date.getFullYear() && month < date.getMonth())
    ) {
      tclass = " class=" + classPast;
    } else if (
      i == today &&
      year == date.getFullYear() &&
      month == date.getMonth()
    ) {
      tclass = " class=" + classToday;
    } else {
      tclass = " class=" + classFuture;
    }
    str += "<li" + tclass + ">" + i + "</li>";
  }
  daysDisplay.innerHTML = str;
  monthElement.innerHTML = month_name[month];
  yearElement.innerHTML = year;
}

function dateStart(month, year) {
  var tmpDate = new Date(year, month, 1);
  var result = tmpDate.getDay();
  if (result == 0) result = 7;
  return result;
}

function numOfDays(month, year) {
  var tmp = year % 4;
  if (tmp == 0) {
    return month_leap[month];
  } else {
    return month_normal[month];
  }
}
refreshDate();

function diary_div(json) {
  var diary = json["diary"];
  var date = diary["date"];
  inner_html = date_div(date);
  for (var i = 0; i < diary["items"].length; i++) {
    inner_html += item_div(diary["items"][i]);
  }
  inner_html += '</div><div id="notes_box" class="notes_box">';
  if (diary.hasOwnProperty("extend")) {
    inner_html += extend_div(diary);
  }
  inner_html += "</div>";
  return inner_html;
}

function diary_div_with_buttons(json) {
  var diary = json["diary"];
  var date = diary["date"];
  inner_html = date_div(date);
  for (var i = 0; i < diary["items"].length; i++) {
    inner_html += item_div_end_with_button(diary["items"][i]);
  }
  inner_html += '</div><div id="notes_box" class="notes_box">';
  if (diary.hasOwnProperty("extend")) {
    inner_html += extend_div(diary);
  }
  inner_html +=
    '</div><div class="button_box"><button class="button" onclick="switch_notes()">Notes</button><button class="button" onclick="add_item()">Add Item</button><button class="button" onclick="request_diary(this)">Complete</button></div>';
  return inner_html;
}

function empty_diary(date) {
  return (
    '<div id="date" class="diary_date"><label class="green" style="font-size: 28px" id="year">' +
    yearElement.innerHTML +
    '-</label><label class="green" style="font-size: 28px" id="month">' +
    month_map[monthElement.innerHTML] +
    '-</label><label class="green" style="font-size: 28px" id="date">' +
    date +
    '</label></div><div id="items" class="items_box"></div></div><div id="notes_box" class="notes_box"></div><div class="button_box"><button class="button" onclick="switch_notes()">Notes</button><button class="button" onclick="add_item()">Add Item</button><button class="button" onclick="request_diary(this)">Complete</button></div>'
  );
}

function extend_div(diary) {
  return (
    '<textarea  id="notes" class="notes">' + diary["extend"] + "</textarea>"
  );
}

function date_div(date_in) {
  return (
    '<div id="date" class="diary_date"><label class="green" style="font-size: 28px" id="year">' +
    date_in["year"] +
    '-</label><label class="green" style="font-size: 28px" id="month">' +
    date_in["month"] +
    '-</label><label class="green" style="font-size: 28px" id="date">' +
    date_in["date"] +
    '</label></div><div id="items" class="items_box">'
  );
}

function item_div(item) {
  return item_base(item) + '"></div>';
}

function item_div_end_with_button(item) {
  return (
    item_base(item) + '">\n<button onclick="delete_item(this)">X</button></div>'
  );
}

function delete_item(button) {
  button.parentElement.remove();
}

empty_item = {
  start: "",
  end: "",
  content: ""
};

function add_item() {
  document.getElementById("items").innerHTML += item_div_end_with_button(empty_item);
}

function switch_notes() {
  var notes = document.getElementById("notes");
  if (notes) {
    notes.remove();
  } else {
    document.getElementById("notes_box").innerHTML =
      '<textarea  id="notes" placeholder="Write notes here." class="notes"></textarea>';
  }
}

function request_diary(button) {
  button.setAttribute("disabled", true)
  var dateObj = make_date()
  var body = make_body(dateObj)
  var xhr = createXmlHttpRequest();
  xhr.onreadystatechange = function () {
    if (xhr.readyState == 4 && xhr.status == 200) {
      alert(xhr.responseText)
    } else if (xhr.readyState == 4) {
      alert("bad request");
    }
    button.disabled = false
    refreshDate()
  };

  if (dates_recorder.indexOf(dateObj["date"]) == -1) {
    xhr.open("POST", "diary", true);
  } else {
    xhr.open("PUT", "diary", true);
  }
  xhr.setRequestHeader('Content-Type', 'application/json');
  xhr.send(body);
}

function make_date() {
  var dateObj = {};
  var diary = document.getElementById("diary")
  var yearElement = diary.firstChild.firstChild
  var year = yearElement.innerText
  year = year.substring(0, year.length - 1)
  var monthElement = yearElement.nextSibling
  var month = monthElement.innerText
  month = month.substring(0, month.length - 1)
  var dateElement = monthElement.nextSibling
  var date = dateElement.innerText

  dateObj["year"] = parseInt(year)
  dateObj["month"] = parseInt(month)
  dateObj["date"] = parseInt(date)
  return dateObj;
}

function make_body(dateObj) {
  var request = {};

  request["date"] = dateObj

  var itemsElement = document.getElementById("items")
  var items = itemsElement.childNodes
  var itemsList = new Array();
  for (var i = 0; i < items.length; i++) {
    var item = {};
    var fromElement = items[i].firstChild.nextSibling
    item["start"] = fromElement.value;
    var toElement = fromElement.nextSibling.nextSibling;
    item["end"] = toElement.value;
    var contentElement = toElement.nextSibling.nextSibling;
    item["content"] = contentElement.value;
    itemsList.push(item)
  }

  var notesElement = document.getElementById("notes")
  if (notesElement != null) {
    request["extend"] = notesElement.value
  }

  request["items"] = itemsList

  return JSON.stringify(request)
}

function item_base(item) {
  return (
    '<div class="item"><label style="margin: 0 5px">' +
    start +
    '</label><input type="text" onkeyup="this.value=this.value.replace(/\\D/g,\'\')" onafterpaste="this.value=this.value.replace(/\\D/g,\'\')" size="2" maxlength="5" value=' +
    item["start"] +
    '><label style="margin: 0 5px">' +
    end +
    '</label><input type="text" onkeyup="this.value=this.value.replace(/\\D/g,\'\')" onafterpaste="this.value=this.value.replace(/\\D/g,\'\')" size="2" maxlength="5" value=' +
    item["end"] +
    '><label style="margin: 0 5px 0 30px">' +
    content +
    '</label><input type="text" size="30" maxlength="50" value="' +
    item["content"]
  );
}

function add_inner_html(parent, response_body, isdbclick, date) {
  var json = JSON.parse(response_body);
  if (json["code"] != 0 && !isdbclick) {
    parent.innerHTML =
      '<div style="text-align: center">No diary this day.<br>Please double click.</div>';
  } else if (json["code"] != 0 && isdbclick) {
    parent.innerHTML = empty_diary(date);
  } else if (isdbclick) {
    parent.innerHTML = diary_div_with_buttons(json);
  } else parent.innerHTML = diary_div(json);
}

function addEventForDates() {
  var days = daysDisplay.getElementsByTagName("li");
  Array.from(days).forEach(function(e) {
    if (e.innerHTML !== "") {
      var click = null;
      e.onclick = function(event) {
        clearTimeout(click);
        click = setTimeout(function() {
          if (lastClass != null && lastClass !== "" && lastElement != null) {
            lastElement.setAttribute("class", lastClass);
          }
          lastClass = e.getAttribute("class");
          lastElement = e;
          e.setAttribute("class", classFocus);
          dateClickEvent(yearElement.innerHTML, monthElement.innerHTML, e.innerHTML, false)
        }, 300);
      };
      e.ondblclick = function(event) {
        clearTimeout(click);
        click = setTimeout(function() {
          if (lastClass != null && lastClass !== "" && lastElement != null) {
            lastElement.setAttribute("class", lastClass);
          }
          lastClass = e.getAttribute("class");
          lastElement = e;
          e.setAttribute("class", classFocus);
          dateClickEvent(yearElement.innerHTML, monthElement.innerHTML, e.innerHTML, true)
        }, 150);
      };
    }
  });
}
