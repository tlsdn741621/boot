// 앞단의, 게시글 작성시,
// 전달할 준비물 1) 게시글 2) 첨부된 이미지, 객체에 담고
// 부트 서버에 전달
// /upload , post 형식 전달,
// axios 를 이용해서, 비동기 통신 이용함.

// 이미지 업로드 기능.
async function uploadToServer (formObj) {
    console.log("upload to server, axios 작업 중")
    console.log(formObj)

    const response = await axios ({
        method: 'post',
        url : '/upload',
        data : formObj,
        headers : {
            'Content-Type' : 'multipart/form-data',
        },
    });
    return response.data
}

// 이미지 삭제
async function removeFileToServer(uuid, fileName) {
    const response = await axios.delete(`/remove/${uuid}_${fileName}`)
    return response.data
}