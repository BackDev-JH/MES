  function search() {
    var selectedOption = document.getElementById("dropdown").value;
    var searchKeyword = document.getElementById("search").value;
    
    // 여기에서 검색 동작을 수행하거나 필요한 로직을 추가하세요.
    
    // 예를 들어, 콘솔에 선택된 옵션과 검색어를 출력하는 경우:
    console.log("선택된 옵션: " + selectedOption);
    console.log("검색어: " + searchKeyword);
  }

  //표 부분
  var table = document.getElementById("myTable3");
    var rowCount = 5; // 초기 행 개수
    var columnData = ["원료계량", "전처리", "추출기1", "추출기2", "혼합 및 살균기1", "혼합 및 살균기2",
        "액상스틱 충진기1", "액상스틱 충진기2", "즙 충진기1", "즙 충진기2", "검사", "냉각", "포장"
    ]; // 첫 번째 열 데이터


  // 특정 셀에 색상을 적용하는 함수
  function applyCellColors() {
      var rows = table.rows;

      for (var i = 1; i < rows.length; i++) { // 첫 번째 행은 헤더이므로 제외
          var cells = rows[i].cells;

          for (var j = 0; j < cells.length; j++) {
              var cell = cells[j];

              if (j === 0) { // 첫 번째 열에만 배경색을 지정
                  cell.style.backgroundColor = "lightgray";
                  cell.style.fontWeight = "bold";
              } else {
                  cell.style.backgroundColor = "white";
                  cell.style.fontWeight = "normal";
              }
          }
      }
  }

    // 페이지 로드 후 행 추가
    window.onload = function() {
        addRow();
        addRow();
        addRow();
        addRow();
        addRow();
        addRow();
        addRow();
        addRow();
        addRow();
        addRow();
        addRow();
        addRow();
        addRow();
        applyCellColors();
    }

    function addRow() {
      var newRow = table.insertRow();
      // 첫 번째 열 데이터 삽입
      var firstCell = newRow.insertCell();
      firstCell.innerHTML = columnData[newRow.rowIndex - 1];

      for (var i = 0; i < rowCount; i++) {
        var cell = newRow.insertCell();
      }
    }

