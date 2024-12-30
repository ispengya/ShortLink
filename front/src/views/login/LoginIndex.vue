<template>
  <div class="login-page">
    <div class="background">
      <div class="shape"></div>
      <div class="shape"></div>
    </div>
    <h1 class="title">链易短</h1>
    <div class="login-container" :class="{ 'flip-login': !isLogin }">
      <!-- 登录 -->
      <div class="login-box">
        <h2>用户登录</h2>
        <el-form ref="loginFormRef1" :model="loginForm" :rules="loginFormRule">
          <el-form-item prop="phone">
            <el-input 
              v-model="loginForm.username" 
              placeholder="请输入用户名" 
              maxlength="11" 
              :prefix-icon="User"
              show-word-limit 
              clearable
            />
          </el-form-item>

          <el-form-item prop="password">
            <el-input 
              v-model="loginForm.password" 
              type="password" 
              :prefix-icon="Lock"
              clearable 
              placeholder="请输入密码" 
              show-password
            />
          </el-form-item>
          
          <div class="form-footer">
            <el-checkbox v-model="checked">记住密码</el-checkbox>
            <el-button 
              :loading="loading" 
              type="primary" 
              class="submit-btn"
              @click="login(loginFormRef1)"
              @keyup.enter="login"
            >
              登录
            </el-button>
          </div>
        </el-form>
        <div class="switch-tip">
          <span>还没有账号？</span>
          <el-button 
            link 
            type="primary" 
            @click="changeLogin"
          >
            去注册
          </el-button>
        </div>
      </div>

      <!-- 注册 -->
      <div class="register-box">
        <h2>用户注册</h2>
        <el-form ref="loginFormRef2" :model="addForm" :rules="addFormRule">
          <el-form-item prop="username">
            <el-input 
              v-model="addForm.username" 
              :prefix-icon="User"
              placeholder="请输入用户名" 
              maxlength="11" 
              show-word-limit 
              clearable
            />
          </el-form-item>
          
          <el-form-item prop="mail">
            <el-input 
              v-model="addForm.mail" 
              :prefix-icon="Message"
              placeholder="请输入邮箱" 
              show-word-limit 
              clearable
            />
          </el-form-item>
          
          <el-form-item prop="phone">
            <el-input 
              v-model="addForm.phone" 
              :prefix-icon="Phone"
              placeholder="请输入手机号" 
              show-word-limit 
              clearable
            />
          </el-form-item>
          
          <el-form-item prop="realName">
            <el-input 
              v-model="addForm.realName" 
              :prefix-icon="UserFilled"
              placeholder="请输入姓名" 
              show-word-limit 
              clearable
            />
          </el-form-item>

          <el-form-item prop="password">
            <el-input 
              v-model="addForm.password" 
              :prefix-icon="Lock"
              type="password" 
              clearable 
              placeholder="请输入密码" 
              show-password
            />
          </el-form-item>

          <div class="form-footer">
            <el-button 
              :loading="loading" 
              type="primary" 
              class="submit-btn"
              @click="addUser(loginFormRef2)"
            >
              注册
            </el-button>
          </div>
        </el-form>
        <div class="switch-tip">
          <span>已有账号？</span>
          <el-button 
            link 
            type="primary" 
            @click="changeLogin"
          >
            去登录
          </el-button>
        </div>
      </div>
    </div>
  </div>

  <!-- 验证码弹窗 -->
  <el-dialog 
    v-model="isWC" 
    title="人机验证" 
    width="400px"
    :before-close="handleClose"
    class="verification-dialog"
  >
    <div class="verification-content">
      <p class="verification-tip">
        扫码下方二维码，关注后回复：
        <strong class="highlight">link</strong>
        获取验证码
      </p>
      <img class="qr-code" src="@/assets/png/公众号二维码.png" alt="二维码">
      <el-form 
        class="verification-form" 
        :model="verification" 
        :rules="verificationRule" 
        ref="verificationRef"
      >
        <el-form-item prop="code">
          <el-input 
            v-model="verification.code"
            placeholder="请输入验证码"
            :prefix-icon="Key"
          />
        </el-form-item>
      </el-form>
    </div>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="isWC = false">取消</el-button>
        <el-button type="primary" @click="verificationLogin(verificationRef)">
          确认
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { setToken, setUsername, getUsername } from '@/core/auth.js'
import { ref, reactive, getCurrentInstance } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, Message, Phone, UserFilled, Key } from '@element-plus/icons-vue'
const { proxy } = getCurrentInstance()
const API = proxy.$API
const loginFormRef1 = ref()
const loginFormRef2 = ref()
const router = useRouter()
const loginForm = reactive({
  username: 'admin',
  password: 'admin123456',
})
const addForm = reactive({
  username: '',
  password: '',
  realName: '',
  phone: '',
  mail: ''
})

const addFormRule = reactive({
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    {
      pattern: /^1[3|5|7|8|9]\d{9}$/,
      message: '请输入正确的手机号',
      trigger: 'blur'
    },
    { min: 11, max: 11, message: '手机号必须是11位', trigger: 'blur' }
  ],
  username: [{ required: true, message: '请输入您的真实姓名', trigger: 'blur' }],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 8, max: 15, message: '密码长度请在八位以上', trigger: 'blur' }
  ],
  mail: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    {
      pattern: /^([a-zA-Z]|[0-9])(\w|\-)+@[a-zA-Z0-9]+\.([a-zA-Z]{2,4})$/,
      message: '请输入正确的邮箱号',
      trigger: 'blur'
    }
  ],
  realNamee: [
    { required: true, message: '请输姓名', trigger: 'blur' },
  ]
})
const loginFormRule = reactive({
  username: [{ required: true, message: '请输入您的真实姓名', trigger: 'blur' }],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 8, max: 15, message: '密码长度请在八位以上', trigger: 'blur' }
  ],
})
// 注册
const addUser = (formEl) => {
  if (!formEl) return
  formEl.validate(async (valid) => {
    if (valid) {
      // 检测用户名是否已经存在
      const res1 = await API.user.hasUsername({ username: addForm.username })
      if (res1.data.success !== false) {
        // 注册
        const res2 = await API.user.addUser(addForm)
        // console.log(res2)
        if (res2.data.success === false) {
          ElMessage.warning(res2.data.message)
        } else {
          const res3 = await API.user.login({ username: addForm.username, password: addForm.password })
          const token = res3?.data?.data?.token
          // 将username和token保存到cookies中和localStorage中
          if (token) {
            setToken(token)
            setUsername(addForm.username)
            localStorage.setItem('token', token)
            localStorage.setItem('username', addForm.username)
          }
          ElMessage.success('注册登录成功！')
          router.push('/home')
        }
      } else {
        ElMessage.warning('用户名已存在！')
      }
    } else {
      return false
    }
  })

}
// 公众号验证码
const isWC = ref(false)
const verificationRef = ref()
const verification = reactive({
  code: ''
})
const verificationRule = reactive({
  code: [{ required: true, message: '请输入验证码', trigger: 'blur' }]
})
const verificationLogin = (formEl) => {
  if (!formEl) return
  formEl.validate(async (valid) => {
    if (valid) {
      const tempPassword = loginForm.password
      loginForm.password = verification.code
      const res1 = await API.user.login(loginForm)
      if (res1.data.code === '0') {
        const token = res1?.data?.data?.token
        // 将username和token保存到cookies中和localStorage中
        if (token) {
          setToken(token)
          setUsername(loginForm.username)
          localStorage.setItem('token', token)
          localStorage.setItem('username', loginForm.username)
        }
        ElMessage.success('登录成功！')
        router.push('/home')
      } else if (res1.data.message === '用户已登录') {
        // 如果已经登录了，判断一下浏览器保存的登录信息是不是再次登录的信息，如果是就正常登录
        const cookiesUsername = getUsername()
        if (cookiesUsername === loginForm.username) {
          ElMessage.success('登录成功！')
          router.push('/home')
        } else {
          ElMessage.warning('用户已在别处登录，请勿重复登录！')
        }
      } else {
        ElMessage.error('请输入正确的验证码!')
      }
      loginForm.password = tempPassword
    }
  })
}
// 登录
const login = (formEl) => {
  if (!formEl) return
  formEl.validate(async (valid) => {
    if (valid) {
      // 当域名为下面这两个时，弹出公众号弹框
      let domain = window.location.host
      if (domain === 'shortlink.magestack.cn' || domain === 'shortlink.nageoffer.com') {
        isWC.value = true
        return
      }
      const res1 = await API.user.login(loginForm)
      if (res1.data.code === '0') {
        const token = res1?.data?.data?.token
        // 将username和token保存到cookies中和localStorage中
        if (token) {
          setToken(token)
          setUsername(loginForm.username)
          localStorage.setItem('token', token)
          localStorage.setItem('username', loginForm.username)
        }
        ElMessage.success('登录成功！')
        router.push('/home')
      } else if (res1.data.message === '用户已登录') {
        // 如果已经登录了，判断一下浏览器保存的登录信息是不是再次登录的信息，如果是就正常登录
        const cookiesUsername = getUsername()
        if (cookiesUsername === loginForm.username) {
          ElMessage.success('登录成功！')
          router.push('/home')
        } else {
          ElMessage.warning('用户已在别处登录，请勿重复登录！')
        }
      } else if (res1.data.message === '用户不存在') {
        ElMessage.error('请输入正确的账号密码!')
      }
    } else {
      return false
    }
  })


}

const loading = ref(false)
// 是否记住密码
const checked = ref(true)

// 展示登录还是展示注册
const isLogin = ref(true)
const changeLogin = () => {
  let domain = window.location.host
  if (domain === 'shortlink.magestack.cn' || domain === 'shortlink.nageoffer.com') {
    ElMessage.warning('演示环境暂不支持注册')
    return
  }
  // 添加延迟重置表单，等待动画完成
  if (isLogin.value) {
    loginFormRef1.value?.resetFields()
  } else {
    loginFormRef2.value?.resetFields()
  }
  isLogin.value = !isLogin.value
}
</script>

<style lang="less" scoped>
.login-page {
  position: relative;
  width: 100vw;
  height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  overflow: hidden;
  background: #f6f5f7;
}

.background {
  position: fixed;
  width: 100vw;
  height: 100vh;
  z-index: 1;
  background: linear-gradient(-45deg, #ee7752, #e73c7e, #23a6d5, #23d5ab);
  background-size: 400% 400%;
  animation: gradient 15s ease infinite;
}

.shape {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(5px);
  
  &:first-child {
    height: 200px;
    width: 200px;
    background: linear-gradient(180deg, rgba(255,255,255,0.1) 0%, rgba(255,255,255,0.05) 100%);
    left: -50px;
    top: -50px;
    animation: float 6s ease-in-out infinite;
  }
  
  &:last-child {
    height: 150px;
    width: 150px;
    background: linear-gradient(180deg, rgba(255,255,255,0.1) 0%, rgba(255,255,255,0.05) 100%);
    right: -30px;
    bottom: -30px;
    animation: float 8s ease-in-out infinite reverse;
  }
}

@keyframes gradient {
  0% {
    background-position: 0% 50%;
  }
  50% {
    background-position: 100% 50%;
  }
  100% {
    background-position: 0% 50%;
  }
}

@keyframes float {
  0% {
    transform: translatey(0px);
  }
  50% {
    transform: translatey(-20px);
  }
  100% {
    transform: translatey(0px);
  }
}

.title {
  margin-top: 5vh;
  font-size: 2rem;
  color: #fff;
  font-weight: 600;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.2);
  z-index: 2;
}

.login-container {
  position: relative;
  width: 400px;
  margin-top: 5vh;
  perspective: 1000px;
  z-index: 2;
}

.login-box,
.register-box {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  padding: 30px;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(10px);
  border-radius: 20px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  transition: transform 0.6s;
  transform-style: preserve-3d;
  backface-visibility: hidden;

  h2 {
    font-size: 24px;
    text-align: center;
    margin-bottom: 25px;
    color: #1a1a1a;
  }

  .el-form-item {
    margin-bottom: 20px;
  }

  :deep(.el-input) {
    --el-input-hover-border-color: #409eff;
    --el-input-focus-border-color: #409eff;
    
    .el-input__wrapper {
      box-shadow: 0 0 0 1px #dcdfe6;
      padding: 1px 15px;
      height: 38px;
      transition: all 0.3s;

      &:hover {
        box-shadow: 0 0 0 1px var(--el-input-hover-border-color);
      }

      &.is-focus {
        box-shadow: 0 0 0 1px var(--el-input-focus-border-color);
      }
    }
  }
}

.login-box {
  transform: rotateY(0deg);
}

.register-box {
  transform: rotateY(180deg);
}

// 添加翻转动画类
.flip-login {
  .login-box {
    transform: rotateY(180deg);
  }
  .register-box {
    transform: rotateY(360deg);
  }
}

.form-footer {
  margin-top: 25px;
  display: flex;
  justify-content: space-between;
  align-items: center;

  .submit-btn {
    width: 100px;
    height: 36px;
    border-radius: 18px;
    font-size: 14px;
  }
}

.switch-tip {
  margin-top: 20px;
  text-align: center;
  color: #666;
  font-size: 14px;

  .el-button {
    padding: 0 4px;
    font-size: 14px;
  }
}

// 验证码弹窗样式
.verification-dialog {
  :deep(.el-dialog) {
    width: 360px !important;
  }

  .verification-content {
    padding: 15px;
    text-align: center;

    .verification-tip {
      font-size: 14px;
      color: #666;
      margin-bottom: 20px;

      .highlight {
        color: #409eff;
        padding: 0 5px;
      }
    }

    .qr-code {
      width: 180px;
      height: 180px;
      margin: 15px auto;
      border-radius: 10px;
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    }

    .verification-form {
      max-width: 280px;
      margin: 20px auto 0;
    }
  }

  .dialog-footer {
    text-align: right;
    margin-top: 20px;
  }
}
</style>
