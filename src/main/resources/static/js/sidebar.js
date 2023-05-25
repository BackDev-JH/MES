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
/* 버려도됨 ~~*/