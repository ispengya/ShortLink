<template>
  <div style="display: flex; height: 100%; width: 100%">
    <div class="options-box">
      <div>
        <span>账号设置</span>
      </div>
    </div>
    <div class="main-box">
      <div class="content-box">
        <div class="info-header">
          <h2>个人信息</h2>
          <el-button type="primary" @click="dialogVisible = !dialogVisible" class="edit-btn">
            <el-icon style="margin-right: 5px"><Edit /></el-icon>修改信息
          </el-button>
        </div>
        <div class="info-grid">
          <div class="info-item">
            <div class="info-label">
              <el-icon><User /></el-icon>
              <span>用户名</span>
            </div>
            <div class="info-value">{{ userInfo?.data?.data?.username }}</div>
          </div>
          <div class="info-item">
            <div class="info-label">
              <el-icon><Iphone /></el-icon>
              <span>手机号</span>
            </div>
            <div class="info-value">{{ userInfo?.data?.data?.phone }}</div>
          </div>
          <div class="info-item">
            <div class="info-label">
              <el-icon><Tickets /></el-icon>
              <span>姓名</span>
            </div>
            <div class="info-value">{{ userInfo?.data?.data?.realName }}</div>
          </div>
          <div class="info-item">
            <div class="info-label">
              <el-icon><Message /></el-icon>
              <span>邮箱</span>
            </div>
            <div class="info-value">{{ userInfo?.data?.data?.mail }}</div>
          </div>
        </div>
      </div>
    </div>
  </div>
  <!-- 修改信息弹窗 -->
  <el-dialog
    v-model="dialogVisible"
    title="修改个人信息"
    width="500px"
    :close-on-click-modal="false"
    destroy-on-close
    class="edit-dialog"
  >
    <div class="edit-form">
      <el-form
        ref="loginFormRef"
        :model="userInfoForm"
        label-width="0"
        class="form-container"
        :rules="formRule"
      >
        <el-form-item prop="username" class="form-item">
          <div class="input-label">
            <el-icon><User /></el-icon>
            <span>用户名</span>
          </div>
          <el-input
            v-model="userInfoForm.username"
            placeholder="请输入用户名"
            maxlength="11"
            disabled
            class="custom-input"
          />
        </el-form-item>

        <el-form-item prop="mail" class="form-item">
          <div class="input-label">
            <el-icon><Message /></el-icon>
            <span>邮箱</span>
          </div>
          <el-input
            v-model="userInfoForm.mail"
            placeholder="请输入邮箱"
            clearable
            class="custom-input"
          />
        </el-form-item>

        <el-form-item prop="phone" class="form-item">
          <div class="input-label">
            <el-icon><Iphone /></el-icon>
            <span>手机号</span>
          </div>
          <el-input
            v-model="userInfoForm.phone"
            placeholder="请输入手机号"
            clearable
            class="custom-input"
          />
        </el-form-item>

        <el-form-item prop="realName" class="form-item">
          <div class="input-label">
            <el-icon><Tickets /></el-icon>
            <span>姓名</span>
          </div>
          <el-input
            v-model="userInfoForm.realName"
            placeholder="请输入姓名"
            clearable
            class="custom-input"
          />
        </el-form-item>

        <el-form-item prop="password" class="form-item">
          <div class="input-label">
            <el-icon><Lock /></el-icon>
            <span>密码</span>
          </div>
          <el-input
            v-model="userInfoForm.password"
            placeholder="默认密码，如需修改可输入新密码"
            clearable
            type="password"
            show-password
            class="custom-input"
          />
        </el-form-item>

        <div class="form-footer">
          <el-button @click="dialogVisible = false" class="cancel-btn">取消</el-button>
          <el-button type="primary" @click="changeUserInfo(loginFormRef)" class="submit-btn">
            确认修改
          </el-button>
        </div>
      </el-form>
    </div>
  </el-dialog>
</template>

<script setup>
import { getCurrentInstance, ref, reactive } from 'vue'
import { getUsername } from '@/core/auth'
import { cloneDeep } from 'lodash'
import { ElMessage } from 'element-plus'
import { User, Iphone, Tickets, Message, Edit, Lock } from '@element-plus/icons-vue'

const loginFormRef = ref()
const { proxy } = getCurrentInstance()
// eslint-disable-next-line no-unused-vars
const API = proxy.$API
const userInfo = ref()
const userInfoForm = ref() // 修改信息
const getUserInfo = async () => {
  const username = getUsername()
  userInfo.value = await API.user.queryUserInfo(username)
  userInfoForm.value = cloneDeep(userInfo.value.data?.data)
  // console.log(userInfoForm.value)
}
getUserInfo()
// 修改信息
const dialogVisible = ref(false)
const formRule = reactive({
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    {
      pattern: /^1[3|5|7|8|9]\d{9}$/,
      message: '请输入正确的手机号',
      trigger: 'blur'
    },
    { min: 11, max: 11, message: '手机号必须是11位', trigger: 'blur' }
  ],
  username: [{ required: true, message: '请输入您的用户名', trigger: 'blur' }],
  mail: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    {
      pattern: /^([a-zA-Z]|[0-9])(\w|\-)+@[a-zA-Z0-9]+\.([a-zA-Z]{2,4})$/,
      message: '请输入正确的邮箱号',
      trigger: 'blur'
    }
  ],
  password: [
    { required: false, message: '请输入密码', trigger: 'blur' },
    { min: 8, max: 15, message: '密码长度请在八位以上', trigger: 'blur' }
  ],
  realNamee: [{ required: true, message: '请输姓名', trigger: 'blur' }]
})
const changeUserInfo = (formEl) => {
  if (!formEl) return
  formEl.validate(async (valid) => {
    if (valid) {
      await API.user.editUser(userInfoForm.value)
      getUserInfo()
      dialogVisible.value = false
      ElMessage.success('修改成功!')
    } else {
      return false
    }
  })
}
</script>

<style lang="scss" scoped>
.main-box {
  position: relative;
  flex: 1;
  padding: 24px;
  background-color: #f5f7fa;
  height: calc(100vh - 50px);
  display: flex;
  flex-direction: column;
}

.content-box {
  flex: 1;
  background-color: #ffffff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
  padding: 24px;
}

.info-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
  padding-bottom: 15px;
  border-bottom: 1px solid #ebeef5;

  h2 {
    margin: 0;
    color: #2c3e50;
    font-size: 20px;
  }
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 24px;
  padding: 0 20px;
}

.info-item {
  padding: 20px;
  background: #f8fafc;
  border-radius: 8px;
  transition: all 0.3s ease;

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  }
}

.info-label {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
  color: #909399;

  .el-icon {
    margin-right: 8px;
    font-size: 18px;
  }

  span {
    font-size: 14px;
  }
}

.info-value {
  color: #2c3e50;
  font-size: 16px;
  font-weight: 500;
}

.options-box {
  position: relative;
  height: 100%;
  width: 190px;
  border-right: 1px solid rgba(0, 0, 0, 0.1);
  display: flex;
  padding-top: 15px;
  
  div {
    flex: 1;
    display: flex;
    height: 50px;
    align-items: center;
    padding-left: 15px;
    background-color: rgb(235, 239, 250);
    font-family: PingFangSC-Semibold, PingFang SC;
    color: #3464e0;
    font-weight: 600;
  }
}

.edit-btn {
  display: flex;
  align-items: center;
  padding: 8px 16px;
}

.register {
  padding-right: 30px;
}

.second-font {
  margin-left: 13px;
}

// 弹窗样式
.edit-dialog {
  :deep(.el-dialog) {
    border-radius: 16px;
    overflow: hidden;
    margin-top: 8vh !important;
    
    .el-dialog__header {
      margin: 0;
      padding: 20px 24px;
      border-bottom: 1px solid #ebeef5;
      
      .el-dialog__title {
        font-size: 18px;
        font-weight: 600;
        color: #2c3e50;
      }
    }
    
    .el-dialog__body {
      padding: 24px;
    }
  }
}

.edit-form {
  .form-item {
    margin-bottom: 16px;
    
    &:last-child {
      margin-bottom: 0;
    }
  }
}

.input-label {
  display: flex;
  align-items: center;
  margin-bottom: 4px;
  
  .el-icon {
    font-size: 14px;
    margin-right: 6px;
    color: #909399;
  }
  
  span {
    font-size: 13px;
    color: #606266;
  }
}

.custom-input {
  :deep(.el-input__wrapper) {
    background-color: #f5f7fa;
    border-radius: 6px;
    box-shadow: none;
    border: 1px solid transparent;
    padding: 4px 8px;
    transition: all 0.3s ease;
    
    &:hover, &.is-focus {
      border-color: #409eff;
      background-color: #fff;
    }
  }
  
  :deep(.el-input__inner) {
    height: 32px;
    font-size: 13px;
    
    &::placeholder {
      color: #c0c4cc;
    }
  }
}

.form-footer {
  display: flex;
  justify-content: flex-end;
  margin-top: 24px;
  padding-top: 16px;
  border-top: 1px solid #ebeef5;
  
  .cancel-btn {
    margin-right: 12px;
  }
  
  .submit-btn {
    min-width: 90px;
  }
}
</style>
