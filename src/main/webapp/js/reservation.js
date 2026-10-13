document.addEventListener("DOMContentLoaded", () => {
    const form = document.getElementById("reservationForm");
    const movie = document.getElementById("movie");
    const showtime = document.getElementById("showtime");

    if (!form || !movie || !showtime || !form.querySelector(".seats")) {
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

    const scheduleOptions = Array.from(showtime.options)
        .filter(option => option.value !== "")
        .map(option => option.cloneNode(true));

    const initialScheduleId = showtime.value;

    let loadedScheduleId = "";
    let loadingSeats = false;
    let seatController = null;
    let seatRequestId = 0;

    // 座席取得状況を表示
    const seatMessage = document.createElement("p");
    seatMessage.setAttribute("role", "status");
    seatMessage.hidden = true;

    form.querySelector(".seats").before(seatMessage);

    function setText(element, text) {
        if (element) {
            element.textContent = text;
        }
    }

    function getCount(element) {
        const value = Number(element?.value ?? 0);

        return Number.isInteger(value) && value >= 0
            ? value
            : 0;
    }

    function disableSeats() {
        form.querySelectorAll('input[name="seat"]').forEach(seat => {
            seat.checked = false;
            seat.disabled = true;
        });

        updateSeats();
    }

    function updateMovie() {
        const option = movie.selectedOptions[0];

        setText(
            summaryMovie,
            movie.value && option ? option.text.trim() : "未選択"
        );
    }

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
            if (option.dataset.movieId !== movie.value) {
                return;
            }

            const copy = option.cloneNode(true);
            copy.defaultSelected = false;
            copy.selected = false;

            showtime.add(copy);
        });

        const exists = Array.from(showtime.options).some(
            option => option.value === selectedId
        );

        showtime.value = exists ? selectedId : "";
        showtime.disabled = movie.value === "";

        updateShowtime();
    }

    function updateSeats() {
        const seats = Array.from(
            form.querySelectorAll(
                'input[name="seat"]:checked:not(:disabled)'
            )
        ).map(seat => seat.value);

        setText(
            summarySeats,
            seats.length > 0 ? seats.join("・") : "未選択"
        );
    }

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

        const total =
            adultCount * 1900 +
            studentCount * 1500 +
            seniorCount * 1200;

        setText(summaryTotal, total.toLocaleString("ja-JP"));
    }

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

    // 取得中の古い上映回を破棄
    function cancelSeatRequest() {
        seatRequestId++;
        seatController?.abort();
        seatController = null;

        loadingSeats = false;
        loadedScheduleId = "";

        form.querySelector(".seats")
            ?.removeAttribute("aria-busy");
    }

    // 上映回の座席状況をAjaxで取得
    async function loadSeats() {
        cancelSeatRequest();

        const requestId = seatRequestId;
        const scheduleId = showtime.value;
        const movieId = movie.value;

        disableSeats();
        updateShowtime();

        seatMessage.hidden = false;

        if (!scheduleId) {
            seatMessage.textContent = "上映回を選択してください。";
            return;
        }

        const controller = new AbortController();
        seatController = controller;
        loadingSeats = true;

        seatMessage.textContent = "座席状況を読み込み中…";

        form.querySelector(".seats")
            .setAttribute("aria-busy", "true");

        // 現在の予約画面Actionへ問い合わせる
        const url = new URL(window.location.href);
        url.search = "";
        url.hash = "";
        url.searchParams.set("scheduleId", scheduleId);

        try {
            const response = await fetch(url.href, {
                headers: {
                    "Accept": "text/html"
                },
                cache: "no-store",
                signal: controller.signal
            });

            if (!response.ok) {
                throw new Error(`HTTP ${response.status}`);
            }

            const html = await response.text();
            const page = new DOMParser().parseFromString(
                html, "text/html"
            );

            const returnedMovie = page.querySelector("#movie");
            const returnedShowtime = page.querySelector("#showtime");
            const newSeats = page.querySelector(".seats");

            if (!newSeats ||
                returnedMovie?.value !== movieId ||
                returnedShowtime?.value !== scheduleId) {
                throw new Error("座席状況を取得できませんでした。");
            }

            // 選択変更後の古い応答は反映しない
            if (requestId !== seatRequestId ||
                showtime.value !== scheduleId ||
                movie.value !== movieId) {
                return;
            }

            const replacement = document.importNode(newSeats, true);

            replacement.querySelectorAll(
                'input[name="seat"]'
            ).forEach(seat => {
                seat.checked = false;
                // disabledはサーバーが返した状態を維持
            });

            replacement.removeAttribute("aria-busy");

            form.querySelector(".seats").replaceWith(replacement);

            loadedScheduleId = scheduleId;
            updateSeats();

            seatMessage.hidden = true;

        } catch (error) {
            if (error.name === "AbortError") {
                return;
            }

            if (requestId === seatRequestId) {
                console.error(error);

                seatMessage.textContent =
                    "座席状況を取得できませんでした。上映回を選び直してください。";
            }

        } finally {
            if (requestId === seatRequestId) {
                loadingSeats = false;
                seatController = null;

                form.querySelector(".seats")
                    ?.removeAttribute("aria-busy");
            }
        }
    }

    // 映画変更
    movie.addEventListener("change", () => {
        cancelSeatRequest();
        filterShowtimes();
        disableSeats();
        updateMovie();

        seatMessage.textContent = "上映回を選択してください。";
        seatMessage.hidden = false;
    });

    // 上映回変更
    showtime.addEventListener("change", loadSeats);

    // 券種変更
    [adult, student, senior].forEach(element => {
        element?.addEventListener("change", updateTickets);
    });

    // 差し替え後の座席にも対応
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

        if (loadingSeats) {
            stop("座席状況の読み込みが完了するまでお待ちください。");
            return;
        }

        if (!movie.value) {
            stop("映画を選択してください。");
            return;
        }

        if (!showtime.value) {
            stop("上映回を選択してください。");
            return;
        }

        if (showtime.selectedOptions[0]?.dataset.movieId !== movie.value) {
            stop("選択した映画に対応する上映回を選んでください。");
            return;
        }

        if (loadedScheduleId !== showtime.value) {
            stop("上映回を選び直し、座席状況を読み込んでください。");
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

    // 初期表示
    filterShowtimes(initialScheduleId);
    updateMovie();
    updateSeats();
    updateTickets();
    updatePayment();

    // スケジュールから来た場合も最新の座席状況を取得
    loadSeats();

    // ブラウザの「戻る」でも座席状況を取得し直す
    window.addEventListener("pageshow", event => {
        if (event.persisted) {
            loadSeats();
        }
    });
});