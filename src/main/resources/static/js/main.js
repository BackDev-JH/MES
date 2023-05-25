
/* ----------------------------------------------*/


    // 달력
    // 현재 날짜 정보 가져오기
    var today = new Date();
    var currentMonth = today.getMonth();
    var currentYear = today.getFullYear();
    var currentDate = today.getDate();

    // 달력 생성 함수
    function generateCalendar(year, month, date) {
      var calendar = document.getElementById("calendar");
      var header = document.getElementById("calendar-header");

      // 기존 달력 초기화
      while (calendar.rows.length > 1) {
        calendar.deleteRow(-1);
      }

      // 월의 첫 번째 날 구하기
      var firstDay = new Date(year, month, 1);
      var startingDay = firstDay.getDay();

      // 월의 총 일수 구하기
      var monthLength = new Date(year, month + 1, 0).getDate();

      // 월 정보 표시
      var monthNames = [
        "1월", "2월", "3월", "4월", "5월", "6월",
        "7월", "8월", "9월", "10월", "11월", "12월"
      ];
      header.innerHTML = monthNames[month] + " 작업 일정"; // 월 정보 변경

      var day = 1;
      var dateSet = false;
      // 달력 행 추가e
      for (var i = 0; i < 5; i++) {
        var row = calendar.insertRow(-1);

        // 요일 별 날짜 채우기
        for (var j = 0; j < 7; j++) {
          if (i === 0 && j < startingDay) {
            var cell = row.insertCell(-1);
            cell.innerHTML = "";
          } else if (day > monthLength) {
            // 빈 공간을 네모칸으로 채우기
            var cell = row.insertCell(-1);
            cell.innerHTML = "";
            cell.className = "empty-cell";
          } else {
            var cell = row.insertCell(-1);
            cell.innerHTML = day;

            // 현재 날짜에 표시 스타일 적용
            if (!dateSet && day === date) {
              cell.style.backgroundColor = "lightblue";
              dateSet = true;
            }



            day++;
          }
        }
      }
    }

    // 초기 달력 생성
    generateCalendar(currentYear, currentMonth, currentDate);


    function updateCalendar(data) {
        // 달력 업데이트 작업 수행

        // 달력 초기화
        var calendar = document.getElementById("calendar");
        while (calendar.rows.length > 1) {
            calendar.deleteRow(-1);
        }

        // 가져온 일정 데이터를 기반으로 달력에 일정 출력
        // 예시로 가져온 일정 데이터는 "일정"이라는 키로 각 날짜에 해당하는 일정을 가지고 있는 배열이라고 가정합니다.
        for (var i = 0; i < 5; i++) {
            var row = calendar.insertRow(-1);
            for (var j = 0; j < 7; j++) {
                var day = i * 7 + j + 1;
                var cell = row.insertCell(-1);
                if (data[day] && data[day].length > 0) {
                    // 해당 날짜에 일정이 있는 경우
                    cell.innerHTML = day + "<br>";
                    for (var k = 0; k < data[day].length; k++) {
                        cell.innerHTML += data[day][k] + "<br>";
                    }
                } else {
                    // 해당 날짜에 일정이 없는 경우
                    cell.innerHTML = day;
                }
            }
        }
    }


    $.ajax({
        url: '/Orders',
        method: 'GET',
        dataType: 'json',
        success: function(data) {
            // 일정 데이터를 성공적으로 가져왔을 때의 처리
            // 달력을 업데이트하는 함수 호출
            updateCalendar(data);
        },
        error: function() {
            // 일정 데이터를 가져오지 못했을 때의 처리
            console.log('일정 데이터를 가져오는데 실패했습니다.');
        }
    });





    /*------------------------------------------------------------------*/
    // 사이드바 
   // 사이드바 버튼 크기 조절
   function adjustButtonSize() {
      var sidebar = document.querySelector('.sidebar');
      var buttons = document.querySelectorAll('.menu-button');
    
      var sidebarWidth = sidebar.offsetWidth;

      buttons.forEach(function(button) {
        var buttonWidth = sidebarWidth - 20; // 여유 공간을 줄여서 버튼의 크기 조정
        button.style.width = sidebarWidth + 'px';

      });
    }






// 초기 사이드바 버튼 크기 조절
adjustButtonSize();

// 사이드바 열기/닫기 버튼 클릭 이벤트 처리
var sidebar = document.querySelector('.sidebar');
var content = document.querySelector('.content');

    function showTab(tabName) {
      var selectedTab = document.getElementById(tabName + "1");
      selectedTab.classList.remove("hidden");
    }
    function toggleSidebar(menu) {
        var sidebar = document.getElementsByClassName("sidebar")[0];
        var sidebarItems = sidebar.querySelectorAll("div[id$='Sidebar']");
        var content = document.querySelector('.content');

        if (sidebar.classList.contains("sidebar-open")) {
            sidebar.classList.remove("sidebar-open");
            content.classList.remove('content-shift');
        } else {
            sidebar.classList.add("sidebar-open");
            content.classList.add('content-shift');
        }

        for (var i = 0; i < sidebarItems.length; i++) {
            var item = sidebarItems[i];
            if (item.id === menu + "Sidebar") {
                item.classList.remove("hidden");
            } else {
                item.classList.add("hidden");
            }
        }
    }