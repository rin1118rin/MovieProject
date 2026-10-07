document.addEventListener("DOMContentLoaded", function () {

    const movie = document.getElementById("movie");
    const showtime = document.getElementById("showtime");

    const adult = document.getElementById("adult");
    const student = document.getElementById("student");
    const senior = document.getElementById("senior");

    const summaryMovie = document.getElementById("summaryMovie");
    const summaryShowtime = document.getElementById("summaryShowtime");
    const summarySeats = document.getElementById("summarySeats");
    const summaryTickets = document.getElementById("summaryTickets");
    const summaryPayment = document.getElementById("summaryPayment");
    const summaryTotal = document.getElementById("summaryTotal");

    // 作品
    function updateMovie() {

        if (movie.selectedIndex <= 0) {
            summaryMovie.textContent = "未選択";
            return;
        }

        summaryMovie.textContent =
            movie.options[movie.selectedIndex].text.trim();
    }


    // 上映日時
    function updateShowtime() {

        if (showtime.selectedIndex <= 0) {
            summaryShowtime.textContent = "未選択";
            return;
        }

        summaryShowtime.textContent =
            showtime.options[showtime.selectedIndex].text.trim();
    }


    // 座席
    function updateSeats() {

        const selectedSeats =
            document.querySelectorAll('input[name="seat"]:checked');

        const seats = [];

        selectedSeats.forEach(function (seat) {
            seats.push(seat.value);
        });

        if (seats.length === 0) {
            summarySeats.textContent = "未選択";
        } else {
            summarySeats.textContent = seats.join("・");
        }
    }


    // 券種・合計金額
    function updateTickets() {

        const adultCount = Number(adult.value);
        const studentCount = Number(student.value);
        const seniorCount = Number(senior.value);

        const tickets = [];

        if (adultCount > 0) {
            tickets.push("一般 × " + adultCount + "枚");
        }

        if (studentCount > 0) {
            tickets.push("学生 × " + studentCount + "枚");
        }

        if (seniorCount > 0) {
            tickets.push("シニア × " + seniorCount + "枚");
        }

        if (tickets.length === 0) {
            summaryTickets.textContent = "未選択";
        } else {
            summaryTickets.textContent = tickets.join(" / ");
        }


        // 合計金額
        const total =
            adultCount * 1900 +
            studentCount * 1500 +
            seniorCount * 1200;

        summaryTotal.textContent = total.toLocaleString();
    }


    // 支払い方法
    function updatePayment() {

        const payment =
            document.querySelector('input[name="payment"]:checked');

        if (!payment) {
            return;
        }

        if (payment.value === "counter") {
            summaryPayment.textContent = "窓口支払い";
        }

        if (payment.value === "card") {
            summaryPayment.textContent = "クレジットカード";
        }
    }


    // ==========================
    // イベント
    // ==========================

    movie.addEventListener("change", updateMovie);

    showtime.addEventListener("change", updateShowtime);

    adult.addEventListener("change", updateTickets);
    student.addEventListener("change", updateTickets);
    senior.addEventListener("change", updateTickets);


    document
        .querySelectorAll('input[name="seat"]')
        .forEach(function (seat) {

            seat.addEventListener("change", updateSeats);

        });


    document
        .querySelectorAll('input[name="payment"]')
        .forEach(function (payment) {

            payment.addEventListener("change", updatePayment);

        });


    // ==========================
    // 予約確定前チェック
    // ==========================

    const form = document.getElementById("reservationForm");

    form.addEventListener("submit", function (event) {

        const seatCount =
            document.querySelectorAll(
                'input[name="seat"]:checked'
            ).length;


        const ticketCount =
            Number(adult.value) +
            Number(student.value) +
            Number(senior.value);


        // チケット0枚
        if (ticketCount === 0) {

            alert("チケットを1枚以上選択してください。");

            event.preventDefault();

            return;
        }


        // 座席未選択
        if (seatCount === 0) {

            alert("座席を選択してください。");

            event.preventDefault();

            return;
        }


        // 座席数とチケット枚数が違う
        if (seatCount !== ticketCount) {

            alert(
                "チケット枚数と座席数を一致させてください。"
            );

            event.preventDefault();

            return;
        }

    });


    // 初期表示
    updateMovie();
    updateShowtime();
    updateSeats();
    updateTickets();
    updatePayment();

});