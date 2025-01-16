//带web前缀，本地时为localhost:8080/web
function getFullCtxPath() {
    var pathName = window.location.pathname.substring(1);
    var webName = pathName === '' ? '' : pathName.substring(0, pathName.indexOf('/'));
    return window.location.protocol + '//' + window.location.host + '/' + webName;
}

// 处理需要登录的操作
function handleLoginRequired() {
    layer.confirm('此功能需要登录，是否去登录？', {
        btn: ['去登录','取消'],
        title: '提示'
    }, function(){
        window.location.href = getFullCtxPath() + '/login';
    });
}

// 处理点赞
function handleLike(type, targetId, onSuccess) {
    // 检查登录状态
    if(!isUserLoggedIn) {
        handleLoginRequired();
        return;
    }
    
    $.ajax({
        url: ctx + "web/interaction/like",
        type: "POST", 
        data: {
            type: type,
            targetId: targetId
        },
        success: function(res) {
            if(res.code === 0) {
                layer.msg('点赞成功');
                if(onSuccess) onSuccess(res);
            } else {
                layer.msg(res.msg);
            }
        }
    });
}

// 处理取消点赞
function handleUnlike(type, targetId, onSuccess) {
    // 检查登录状态
    if(!isUserLoggedIn) {
        handleLoginRequired();
        return;
    }
    
    $.ajax({
        url: ctx + "web/interaction/unlike",
        type: "POST",
        data: {
            type: type,
            targetId: targetId
        },
        success: function(res) {
            if(res.code === 0) {
                layer.msg('已取消点赞');
                if(onSuccess) onSuccess(res);
            } else {
                layer.msg(res.msg);
            }
        }
    });
}

// 处理收藏
function handleCollect(type, targetId, onSuccess) {
    // 检查登录状态
    if(!isUserLoggedIn) {
        handleLoginRequired();
        return;
    }
    
    $.ajax({
        url: ctx + "web/interaction/collect",
        type: "POST",
        data: {
            type: type,
            targetId: targetId
        },
        success: function(res) {
            if(res.code === 0) {
                layer.msg('收藏成功');
                if(onSuccess) onSuccess(res);
            } else {
                layer.msg(res.msg);
            }
        }
    });
}

// 处理取消收藏
function handleUncollect(type, targetId, onSuccess) {
    // 检查登录状态
    if(!isUserLoggedIn) {
        handleLoginRequired();
        return;
    }
    
    $.ajax({
        url: ctx + "web/interaction/uncollect",
        type: "POST",
        data: {
            type: type,
            targetId: targetId
        },
        success: function(res) {
            if(res.code === 0) {
                layer.msg('已取消收藏');
                if(onSuccess) onSuccess(res);
            } else {
                layer.msg(res.msg);
            }
        }
    });
}