import{_ as C,g as V,u as M,r,o as S,q as u,c as t,d as U,e as b,h as o,i as n,f as s,j as i,t as B,v as E,x as H,y as N,E as R}from"./index-720fbcc6.js";const T={class:"common-layout"},q={class:"header"},A={style:{display:"flex","align-items":"center"}},P={class:"block"},j={class:"name-span",style:{"text-decoration":"none"}},D={class:"content-box"},K={__name:"HomeIndex",setup($){const{proxy:p}=V(),l=p.$API,c=M();r("https://cube.elemecdn.com/9/c2/f0ee8a3c7c9638a54940382568c9dpng.png");const m=()=>{c.push("/home/account")},h=async()=>{const e=E(),a=u();await l.user.logout({token:e,username:a}),H(),N(),localStorage.removeItem("token"),localStorage.removeItem("username"),c.push("/login"),R.success("成功退出！")},g=()=>{c.push("/home/space")},_=r("");S(async()=>{const e=u();await l.user.queryUserInfo(e),_.value=f(e,8)});const f=(e,a)=>e.length>a?e.slice(0,a)+"...":e;return(e,a)=>{const d=t("el-dropdown-item"),v=t("el-dropdown-menu"),y=t("el-dropdown"),x=t("el-header"),w=t("RouterView"),I=t("el-main"),k=t("el-container");return U(),b("div",T,[o(k,null,{default:n(()=>[o(x,{height:"54px",style:{padding:"0"}},{default:n(()=>[s("div",q,[s("div",{onClick:g,class:"logo"},"链易短-快速生成专属于你的短链接"),s("div",A,[o(y,null,{dropdown:n(()=>[o(v,null,{default:n(()=>[o(d,{onClick:m},{default:n(()=>[i("个人信息")]),_:1}),o(d,{divided:"",onClick:h},{default:n(()=>[i("退出")]),_:1})]),_:1})]),default:n(()=>[s("div",P,[s("span",j,B(_.value),1)])]),_:1})])])]),_:1}),o(I,{style:{padding:"0"}},{default:n(()=>[s("div",D,[o(w,{class:"content-space"})])]),_:1})]),_:1})])}}},F=C(K,[["__scopeId","data-v-f6ac5ee2"]]);export{F as default};
