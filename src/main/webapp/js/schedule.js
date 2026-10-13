document.addEventListener("DOMContentLoaded", () => {
    const dateList = document.querySelector(".schedule-date-list");

    if (!dateList) {
        return;
    }

    let loading = false;

    const errorMessage = document.createElement("p");
    errorMessage.setAttribute("role", "alert");
    errorMessage.style.color = "#c9143b";
    errorMessage.hidden = true;
    dateList.after(errorMessage);

    dateList.addEventListener("click", async (event) => {
        const link = event.target.closest("a.schedule-date");

        if (!link || !dateList.contains(link)) {
            return;
        }

        if (event.ctrlKey || event.metaKey ||
            event.shiftKey || event.altKey) {
            return;
        }

        event.preventDefault();

        if (loading) {
            return;
        }

        const currentContent =
            document.querySelector(".schedule-content");

        if (!currentContent) {
            return;
        }

        loading = true;
        errorMessage.hidden = true;
        currentContent.setAttribute("aria-busy", "true");

        try {
            const response = await fetch(link.href, {
                headers: {
                    "Accept": "text/html"
                }
            });

            if (!response.ok) {
                throw new Error(`HTTP ${response.status}`);
            }

            const html = await response.text();
            const page = new DOMParser().parseFromString(
                html, "text/html"
            );

            const newContent =
                page.querySelector(".schedule-content");

            if (!newContent) {
                throw new Error("上映一覧を取得できませんでした。");
            }

            // ページ全体ではなく、上映一覧だけを更新
            currentContent.replaceWith(
                document.importNode(newContent, true)
            );

            dateList.querySelectorAll(".schedule-date")
                .forEach((item) => {
                    item.classList.remove("active");
                    item.removeAttribute("aria-current");
                });

            link.classList.add("active");
            link.setAttribute("aria-current", "date");

        } catch (error) {
            console.error(error);
            errorMessage.textContent =
                "上映一覧の取得に失敗しました。もう一度日付を押してください。";
            errorMessage.hidden = false;

        } finally {
            loading = false;
            currentContent.removeAttribute("aria-busy");
        }
    });
});
