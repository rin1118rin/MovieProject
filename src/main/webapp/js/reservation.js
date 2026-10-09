document.addEventListener("DOMContentLoaded", () => {
    const form = document.getElementById("reservationForm");
    const movie = document.getElementById("movie");
    const showtime = document.getElementById("showtime");

    if (!form || !movie || !showtime) {
        return;
    }

    const adult = document.getElementById("adult");
    const student = document.getElementById("student");
    const senior = document.getElementById("senior");

    const summaryMovie = document.getElementById("summaryMovie");
    const summaryShowtime = document.getElementById("summaryShowtime");
    const summarySeats = document.getElementById("summarySeats");
    const summaryTickets = document.getElementById("summaryTickets");
    const summaryPayment = document.getElementById("summaryPayment");
    const summaryTotal = document.getElementById("summaryTotal");

    // Actionから渡された上映回の選択肢を保存
    const scheduleOptions = Array.from(showtime.options)
        .filter(option => option.value !== "")
        .map(option => option.cloneNode(true));

    const initialScheduleId = showtime.value;

    function setText(element, text) {
        if (element) {
            element.textContent = text;
        }
    }

    function getCount(element) {
        const count = Number(element?.value ?? 0);
        return Number.isInteger(count) && count >= 0 ? count : 0;
    }

    // 映画名の表示
    function updateMovie() {
        const option = movie.selectedOptions[0];

        setText(
            summaryMovie,
            movie.value && option ? option.text.trim() : "未選択"
        );
    }

    // 上映日時の表示
    function updateShowtime() {
        const option = showtime.selectedOptions[0];

        setText(
            summaryShowtime,
            showtime.value && option ? option.text.trim() : "未選択"
        );
    }

    // 選択した映画の上映回だけを表示
    function filterShowtimes(selectedId = "") {
        showtime.replaceChildren(
            new Option("上映回を選択してください", "")
        );

        scheduleOptions.forEach(option => {
            if (option.dataset.movieId === movie.value) {
                const copy = option.cloneNode(true);

                // 保存時の選択状態を解除
                copy.defaultSelected = false;
                copy.selected = false;

                showtime.add(copy);
            }
        });

        const exists = Array.from(showtime.options).some(
            option => option.value === selectedId
        );

        showtime.value = exists ? selectedId : "";
        showtime.disabled = movie.value === "";

        updateShowtime();
    }

    // 座席の選択を解除
    function clearSelectedSeats() {
        form.querySelectorAll('input[name="seat"]').forEach(seat => {
            seat.checked = false;
        });

        updateSeats();
    }

    // 選択座席の表示
    function updateSeats() {
        const seats = Array.from(
            form.querySelectorAll('input[name="seat"]:checked')
        ).map(seat => seat.value);

        setText(
            summarySeats,
            seats.length > 0 ? seats.join("・") : "未選択"
        );
    }

    // 券種・合計金額の表示
    function updateTickets() {
        const adultCount = getCount(adult);
        const studentCount = getCount(student);
        const seniorCount = getCount(senior);

        const tickets = [];

        if (adultCount > 0) {
            tickets.push(`一般 × ${adultCount}枚`);
        }

        if (studentCount > 0) {
            tickets.push(`学生 × ${studentCount}枚`);
        }

        if (seniorCount > 0) {
            tickets.push(`シニア × ${seniorCount}枚`);
        }

        setText(
            summaryTickets,
            tickets.length > 0 ? tickets.join(" / ") : "未選択"
        );

        // 現在の料金設定
        const total =
            adultCount * 1900 +
            studentCount * 1500 +
            seniorCount * 1200;

        setText(summaryTotal, total.toLocaleString("ja-JP"));
    }

    // 支払い方法の表示
    function updatePayment() {
        const payment = form.querySelector(
            'input[name="payment"]:checked'
        );

        const labels = {
            counter: "窓口支払い",
            card: "クレジットカード"
        };

        setText(
            summaryPayment,
            payment ? (labels[payment.value] ?? "未選択") : "未選択"
        );
    }

    // 映画を変更
    movie.addEventListener("change", () => {
        filterShowtimes();
        clearSelectedSeats();
        updateMovie();
    });

    // 上映回を変更
    showtime.addEventListener("change", () => {
        clearSelectedSeats();
        updateShowtime();
    });

    // 券種の枚数を変更
    [adult, student, senior].forEach(element => {
        element?.addEventListener("change", updateTickets);
    });

    // 座席・支払い方法を変更
    form.addEventListener("change", event => {
        if (event.target.matches('input[name="seat"]')) {
            updateSeats();
        }

        if (event.target.matches('input[name="payment"]')) {
            updatePayment();
        }
    });

    // 送信前チェック
    form.addEventListener("submit", event => {
        function stop(message) {
            event.preventDefault();
            alert(message);
        }

        if (!movie.value) {
            stop("映画を選択してください。");
            return;
        }

        if (!showtime.value) {
            stop("上映回を選択してください。");
            return;
        }

        const selectedOption = showtime.selectedOptions[0];

        if (selectedOption.dataset.movieId !== movie.value) {
            stop("選択した映画に対応する上映回を選んでください。");
            return;
        }

        const ticketCount =
            getCount(adult) +
            getCount(student) +
            getCount(senior);

        const seatCount = form.querySelectorAll(
            'input[name="seat"]:checked:not(:disabled)'
        ).length;

        if (ticketCount === 0) {
            stop("チケットを1枚以上選択してください。");
            return;
        }

        if (seatCount === 0) {
            stop("座席を選択してください。");
            return;
        }

        if (seatCount !== ticketCount) {
            stop("チケット枚数と座席数を一致させてください。");
            return;
        }

        if (!form.querySelector('input[name="payment"]:checked')) {
            stop("支払い方法を選択してください。");
        }
    });

    // 初期表示：スケジュールから来た場合の選択を維持
    filterShowtimes(initialScheduleId);
    updateMovie();
    updateSeats();
    updateTickets();
    updatePayment();
});