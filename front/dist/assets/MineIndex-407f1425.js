import{_ as E,r as y,g as P,a as R,c as r,d as k,e as C,f as t,h as e,i as l,F as Z,q as j,P as b,j as d,t as h,D as L,n as T,p as G,l as H,E as J}from"./index-720fbcc6.js";import{l as K}from"./lodash-5b9fd961.js";const w=f=>(G("data-v-9ef1015c"),f=f(),H(),f),O={style:{display:"flex",height:"100%",width:"100%"}},Q=w(()=>t("div",{class:"options-box"},[t("div",null,[t("span",null,"账号设置")])],-1)),W={class:"main-box"},X={class:"cell-item"},Y={key:0},ee={class:"cell-item"},le={class:"cell-item"},ae={class:"cell-item"},se=w(()=>t("span",{class:"second-font"},"箱",-1)),te=w(()=>t("span",{class:"second-font"},"名",-1)),oe=w(()=>t("span",{class:"second-font"},"码",-1)),ne={style:{width:"100%",display:"flex","justify-content":"flex-end"}},re={__name:"MineIndex",setup(f){const x=y(),{proxy:N}=P(),I=N.$API,m=y(),o=y(),U=async()=>{var s;const n=j();m.value=await I.user.queryUserInfo(n),o.value=K.cloneDeep((s=m.value.data)==null?void 0:s.data)};U();const p=y(!1),S=R({phone:[{required:!0,message:"请输入手机号",trigger:"blur"},{pattern:/^1[3|5|7|8|9]\d{9}$/,message:"请输入正确的手机号",trigger:"blur"},{min:11,max:11,message:"手机号必须是11位",trigger:"blur"}],username:[{required:!0,message:"请输入您的用户名",trigger:"blur"}],mail:[{required:!0,message:"请输入邮箱",trigger:"blur"},{pattern:/^([a-zA-Z]|[0-9])(\w|\-)+@[a-zA-Z0-9]+\.([a-zA-Z]{2,4})$/,message:"请输入正确的邮箱号",trigger:"blur"}],password:[{required:!1,message:"请输入密码",trigger:"blur"},{min:8,max:15,message:"密码长度请在八位以上",trigger:"blur"}],realNamee:[{required:!0,message:"请输姓名",trigger:"blur"}]}),z=n=>{n&&n.validate(async s=>{if(s)await I.user.editUser(o.value),U(),p.value=!1,J.success("修改成功!");else return!1})};return(n,s)=>{const q=r("user"),g=r("el-icon"),v=r("el-descriptions-item"),M=r("iphone"),$=r("tickets"),A=r("Message"),F=r("el-descriptions"),V=r("el-button"),_=r("el-input"),c=r("el-form-item"),B=r("el-form"),D=r("el-dialog");return k(),C(Z,null,[t("div",O,[Q,t("div",W,[e(F,{class:"margin-top content-box",title:"个人信息",column:1,size:n.size,border:""},{default:l(()=>[e(v,null,{label:l(()=>[t("div",X,[e(g,{style:b(n.iconStyle)},{default:l(()=>[e(q)]),_:1},8,["style"]),d(" 用户名 ")])]),default:l(()=>{var a,i,u;return[p.value?L("",!0):(k(),C("span",Y,h((u=(i=(a=m.value)==null?void 0:a.data)==null?void 0:i.data)==null?void 0:u.username),1))]}),_:1}),e(v,null,{label:l(()=>[t("div",ee,[e(g,{style:b(n.iconStyle)},{default:l(()=>[e(M)]),_:1},8,["style"]),d(" 手机号 ")])]),default:l(()=>{var a,i,u;return[t("span",null,h((u=(i=(a=m.value)==null?void 0:a.data)==null?void 0:i.data)==null?void 0:u.phone),1)]}),_:1}),e(v,null,{label:l(()=>[t("div",le,[e(g,{style:b(n.iconStyle)},{default:l(()=>[e($)]),_:1},8,["style"]),d(" 姓名 ")])]),default:l(()=>{var a,i,u;return[t("span",null,h((u=(i=(a=m.value)==null?void 0:a.data)==null?void 0:i.data)==null?void 0:u.realName),1)]}),_:1}),e(v,null,{label:l(()=>[t("div",ae,[e(g,{style:b(n.iconStyle)},{default:l(()=>[e(A)]),_:1},8,["style"]),d(" 邮箱 ")])]),default:l(()=>{var a,i,u;return[t("span",null,h((u=(i=(a=m.value)==null?void 0:a.data)==null?void 0:i.data)==null?void 0:u.mail),1)]}),_:1})]),_:1},8,["size"]),e(V,{style:{position:"absolute",left:"35px",top:"250px"},type:"primary",onClick:s[0]||(s[0]=a=>p.value=!p.value)},{default:l(()=>[d("修改个人信息")]),_:1})])]),e(D,{modelValue:p.value,"onUpdate:modelValue":s[8]||(s[8]=a=>p.value=a),title:"修改个人信息",width:"60%","before-close":n.handleClose},{default:l(()=>[t("div",{class:T(["register",{hidden:n.isLogin}])},[e(B,{ref_key:"loginFormRef",ref:x,model:o.value,"label-width":"50px",class:"form-container",width:"width",rules:S},{default:l(()=>[e(c,{prop:"username"},{default:l(()=>[e(_,{modelValue:o.value.username,"onUpdate:modelValue":s[1]||(s[1]=a=>o.value.username=a),placeholder:"请输入用户名",maxlength:"11","show-word-limit":"",disabled:""},{prepend:l(()=>[d(" 用户名 ")]),_:1},8,["modelValue"])]),_:1}),e(c,{prop:"mail"},{default:l(()=>[e(_,{modelValue:o.value.mail,"onUpdate:modelValue":s[2]||(s[2]=a=>o.value.mail=a),placeholder:"请输入邮箱","show-word-limit":"",clearable:""},{prepend:l(()=>[d(" 邮"),se]),_:1},8,["modelValue"])]),_:1}),e(c,{prop:"phone"},{default:l(()=>[e(_,{modelValue:o.value.phone,"onUpdate:modelValue":s[3]||(s[3]=a=>o.value.phone=a),placeholder:"请输入手机号","show-word-limit":"",clearable:""},{prepend:l(()=>[d(" 手机号 ")]),_:1},8,["modelValue"])]),_:1}),e(c,{prop:"realName"},{default:l(()=>[e(_,{modelValue:o.value.realName,"onUpdate:modelValue":s[4]||(s[4]=a=>o.value.realName=a),placeholder:"请输入姓名","show-word-limit":"",clearable:""},{prepend:l(()=>[d(" 姓"),te]),_:1},8,["modelValue"])]),_:1}),e(c,{prop:"password"},{default:l(()=>[e(_,{modelValue:o.value.password,"onUpdate:modelValue":s[5]||(s[5]=a=>o.value.password=a),placeholder:"默认密码，如需修改可输入新密码","show-word-limit":"",clearable:""},{prepend:l(()=>[d(" 密"),oe]),_:1},8,["modelValue"])]),_:1}),e(c,null,{default:l(()=>[t("div",ne,[e(V,{onClick:s[6]||(s[6]=a=>p.value=!1)},{default:l(()=>[d("取消")]),_:1}),e(V,{type:"primary",onClick:s[7]||(s[7]=a=>z(x.value))},{default:l(()=>[d(" 提交 ")]),_:1})])]),_:1})]),_:1},8,["model","rules"])],2)]),_:1},8,["modelValue","before-close"])],64)}}},ue=E(re,[["__scopeId","data-v-9ef1015c"]]);export{ue as default};
