/**
 * 댓글 관련 시스템 스크립트
 */

function toggleReply(id) {
    const target = document.getElementById('reply-form-' + id);
    if (target) {
        target.style.display = (target.style.display === 'none') ? 'block' : 'none';
    }
}


function loadMoreChildren(parentId, page) {
    const size = 10;

    fetch(`/comments/${parentId}/children?page=${page}&size=${size}`)
        .then(response => {
            if (!response.ok) throw new Error("네트워크 응답 에러");
            return response.json();
        })
        .then(data => {
            const container = document.getElementById('children-container-' + parentId);
            const moreArea = document.getElementById('more-area-' + parentId);

            if (!container || !moreArea) return;

            // 데이터 렌더링
            data.content.forEach(child => {
                const childHtml = createChildCommentHtml(child);
                moreArea.insertAdjacentHTML('beforebegin', childHtml);
            });

            // 페이지네이션 상태 갱신
            updateMoreButton(moreArea, parentId, page, data);
        })
        .catch(error => {
            console.error('Error:', error);
            alert("데이터를 불러오는 중 오류가 발생했습니다.");
        });
}


function createChildCommentHtml(child) {
    return `
        <div class="reply-section mt-2 ms-4 p-2 border-start border-3 bg-light rounded">
            <div class="d-flex justify-content-between">
                <div>
                    <span class="fw-bold small">${child.writerName}</span>
                    <small class="text-muted ms-1" style="font-size: 0.7rem;">${child.createdAt}</small>
                    <p class="small mb-0 mt-1">${child.content}</p>
                </div>
            </div>
        </div>`;
}

// 더보기 버튼 상태 업데이트
function updateMoreButton(moreArea, parentId, currentPage, data) {
    const nextBtn = moreArea.querySelector('button');
    if (data.last) {
        moreArea.remove();
    } else {
        const nextPage = currentPage + 1;
        nextBtn.setAttribute('onclick', `loadMoreChildren(${parentId}, ${nextPage})`);

        // 남은 개수 계산 (이성적 지표 제공)
        const remaining = data.totalElements - (5 + (nextPage * data.size));
        const displayCount = remaining > 0 ? remaining : 0;
        nextBtn.innerText = `대댓글 더보기... (남은 개수: 약 ${displayCount}개)`;
    }
}