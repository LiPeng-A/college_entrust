const cgTop = {
    template: "<!-- Site Header -->\n" +
    "<header class=\"header\">\n" +
    "\t<div class=\"container\">\n" +
    "<span v-if='user && user.username'>尊敬的用户：<span style='color:#b3e5fc;'> {{user.username}}</span>高校互助网欢迎您！" +
    "<a href='javascript:void(0)' @click='logout' style='color: #00E676'>[退出登录]</a></span>" +
    "<span v-else>请<a style='color: #00e5ff' href='javascript:void(0)' @click='gotoLogin'>登录</a>" +
    "&nbsp; &nbsp; &nbsp; &nbsp;<span><a style='color: #00e5ff' href='register.html' target='_blank'>免费注册</a></span>" +
    "</span>" +
    "\t\t<div class=\"row\">\n" +
    "\t\t\t<div class=\"col-sm-2\">\n" +
    "\t\t\t\t<a  class=\"animsition-link site-logo\" href=\"index.html\" >\n" +
    "\t\t\t\t\t<img src=\"images/site-logo.png\" alt>\n" +
    "\t\t\t\t</a>\n" +
    "\t\t\t\t<span class=\"mobile-menu-icon visible-xs\">\n" +
    "\t\t\t\t\t<a id=\"btn-nav\" href=\"#\">\n" +
    "\t\t\t\t\t\t<span></span>\n" +
    "\t\t\t\t\t\t<span></span>\n" +
    "\t\t\t\t\t\t<span></span>\n" +
    "\t\t\t\t\t</a>\n" +
    "\t\t\t\t</span> \n" +
    "\t\t\t</div>\n" +
    "\t\t\t<div class=\"col-sm-10\">\n" +
    "\t\t\t\t<nav id=\"site-nav\" class=\"nav-toggle\">\n" +
    "\t\t\t\t\t<ul class=\"menu\">\n" +
    "\t\t\t\t\t\t<li><a href=\"index.html\" >首页</a></li>\n" +
    "\t\t\t\t\t\t<li><a href=\"about.html\" >关于</a></li>\n" +
    "\t\t\t\t\t\t<li class=\"is-dropdown\">\n" +
    "\t\t\t\t\t\t\t<a href=\"helper.html\">帮助别人\n" +
    "\t\t\t\t\t\t\t</a>\n" +
    "\t\t\t\t\t\t</li> \n" +
    "\t\t\t\t\t\t\n" +
    "\t\t\t\t\t\t<li class=\"is-dropdown\">\n" +
    "\t\t\t\t\t\t\t<a href=\"findHelp.html\">寻求帮助\n" +
    "                            </a>\n" +
    "\t\t\t\t\t\t</li>\n" +
    "\t\t\t\t\t\t<li><a  href=\"userInfo.html\">个人信息</a>" +
    "\t<ul v-if='user!==null'>\n" +
    "\t\t\t\t\t\t\t\t\t\t<li>用户名:<span style='font-size:20px ' v-text='user.username'></span></li>\n" +
    "\t\t\t\t\t\t\t\t\t\t<li v-if='num!==0'><a  href=\"messageInfo.html\">您有<span v-text='num' style='font-size: 18px'></span>条新的消息</a></li>\n" +
    "\t\t\t\t\t\t\t\t\t\t<li><a  href=\"findHelp.html\">我发布的委托</a></li>\n" +
    "\t\t\t\t\t\t\t\t\t\t<li><a  href=\"acceptHelp.html\">我接收的委托</a></li>\n" +
    "\t\t\t\t\t\t\t\t\t\t<li><a  href=\"alertInfo.html\">修改个人信息</a></li>\n" +
    "\t\t\t\t\t\t\t\t\t\t<li><a  href=\"alertPassword.html\">修改密码</a></li>\n" +
    "\t\t\t\t\t\t\t\t\t\t<li  v-if='isFace===0'><a  href=\"javascript:void(0)\" @click='registerFace'>录入人脸信息</a></li>\n" +
    "\t\t\t\t\t\t\t\t\t\t<li v-else-if='isFace===1'><a  href=\"faceManage.html\" >人脸信息管理</a></li>\n" +
    "\t\t\t\t\t\t\t\t\t\t<li><a  href=\"javascript:void(0)\" @click='logout()'>退出登录</a></li>\n" +
    "\t\t\t\t\t\t\t\t\t</ul>" +
    "</li>\n" +
    "\t\t\t\t\t\t<li v-if='user===null'><a  href=\"login.html\"> 登录</a></li>\n" +
    "\t\t\t\t\t\t<li v-else-if='user===null'><a  href=\"register.html\">注册</a></li>\n" +
    "\t\t\t\t\t\t<li v-else><a  href=\"messageInfo.html\">通知<span class=\"badge badge-light\"v-if='num!==0' v-text='num' ></span></a></li>\n" +
    "\t\t\t\t\t</ul>\n" +
    "\t\t\t\t</nav>\n" +
    "\t\t\t</div>\n" +
    "\t\t</div> \n" +
    "\t</div>\n" +
    "</header>",
    name: 'cg-Top',
    data() {
        return {
            user: null,
            num: 0,
            isFace:0
        }
    },
    created() {
        cg.http.get("/auth/verify")
            .then(resp => {
                this.user = resp.data;
                cg.http.get("user/userDetail/"+this.user.id+"").then(resp=>{
                    this.isFace=resp.data.is_face;
                });
                cg.http.get("/message/info/" + this.user.id + "").then(resp => {
                    this.num = resp.data;
                }).catch(error => {
                });

            });
    },
    methods: {
        gotoLogin() {
            window.location = "/login.html?returnUrl=" + window.location;
        },
        logout() {
            cg.http.put("/auth/logout").then(resp => {
                window.location = "/index.html?returnUrl=" + window.location;
            }).catch(error => {
                alert("注销失败");
            })
        },
        registerFace() {
            //确认用户已经登录的话
            window.location.href="faceRegister.html?id="+this.user.id;
        }

    }
}
export default cgTop;