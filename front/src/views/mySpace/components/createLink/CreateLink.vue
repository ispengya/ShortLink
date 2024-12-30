<template>
  <div class="create-link-form">
    <el-form ref="ruleFormRef" :model="formData" :rules="formRule" label-width="80px">
      <el-form-item label="跳转链接" prop="originUrl">
        <el-input
          v-if="isSingle"
          v-model="formData.originUrl"
          placeholder="请输入http://或https://开头的链接或应用跳转链接"
          class="custom-input"
        />
        <el-input
          v-else
          :rows="3"
          v-model="formData.originUrl"
          type="textarea"
          placeholder="请输入http://或https://开头的链接或应用跳转链接，一行一个，最多100行"
          class="custom-input"
        />
      </el-form-item>

      <el-form-item label="描述信息" prop="describe">
        <el-input
          maxlength="100"
          show-word-limit
          v-loading="isLoading"
          :rows="3"
          v-model="formData.describe"
          type="textarea"
          placeholder="请输入描述信息"
          class="custom-input"
        />
        <span class="row-count">将创建 {{ describeRows }} 条短链</span>
      </el-form-item>

      <el-form-item label="短链分组" prop="gid">
        <el-select v-model="formData.gid" placeholder="请选择" class="custom-select">
          <el-option
            v-for="item in groupInfo"
            :key="item.gid"
            :label="item.name"
            :value="item.gid"
          />
        </el-select>
      </el-form-item>

      <el-form-item label="有效期" prop="v" class="validity-item">
        <el-radio-group v-model="formData.validDateType">
          <el-radio :label="0">永久</el-radio>
          <el-radio :label="1">自定义</el-radio>
        </el-radio-group>
      </el-form-item>

      <el-form-item v-if="formData.validDateType === 1" label="选择时间" class="date-picker-item">
        <div class="date-picker-wrapper">
          <el-date-picker
            :disabled-date="disabledDate"
            v-model="formData.validDate"
            value-format="YYYY-MM-DD HH:mm:ss"
            type="datetime"
            placeholder="选择日期"
            :shortcuts="shortcuts"
            class="custom-date-picker"
          />
          <span class="alert">链接失效后将自动跳转到404页面 !</span>
        </div>
      </el-form-item>

      <div class="form-footer">
        <el-button @click="cancel">取消</el-button>
        <el-button
          type="primary"
          :disabled="submitDisable"
          @click="onSubmit(ruleFormRef)"
        >确认</el-button>
      </div>
    </el-form>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted, nextTick, watch, onBeforeUnmount, getCurrentInstance } from 'vue'
import { ElMessage } from 'element-plus'
import { useStore } from 'vuex'

const store = useStore()
const defaultDomain = store.state.domain ?? ' '
const props = defineProps({
  groupInfo: Array,
  isSingle: Boolean, // 单个创建传true， 批量创建传false,
  defaultGid: String
})
const { proxy } = getCurrentInstance()
const API = proxy.$API
// url的校验规则
const reg =
  /^(https?:\/\/(([a-zA-Z0-9]+-?)+[a-zA-Z0-9]+\.)+(([a-zA-Z0-9]+-?)+[a-zA-Z0-9]+))(:\d+)?(\/.*)?(\?.*)?(#.*)?$/
// 自定义时间中选择几天
const shortcuts = [
  {
    text: '一天',
    value: () => {
      const date = new Date()
      date.setTime(date.getTime() + 3600 * 1000 * 24)
      return date
    }
  },
  {
    text: '七天',
    value: () => {
      const date = new Date()
      date.setTime(date.getTime() + 3600 * 1000 * 24 * 7)
      return date
    }
  },
  {
    text: '三十天',
    value: () => {
      const date = new Date()
      date.setTime(date.getTime() + 3600 * 1000 * 24 * 30)
      return date
    }
  }
]
// eslint-disable-next-line vue/no-dupe-keys
const groupInfo = ref()
const formData = reactive({
  domain: defaultDomain,
  originUrl: null,
  gid: null,
  createdType: 1,
  validDate: null,
  describe: null,
  validDateType: 0
})
watch(
  () => formData,
  nV => {
  },
  {
    deep: true
  }
)
const initFormData = () => {
  formData.domain = defaultDomain
  formData.originUrl = null
  formData.createdType = 1
  formData.validDate = null
  formData.describe = null
  formData.validDateType = 0
}
const maxOriginUrlRows = ref(100) // 最多多少行
// 链接有多少行
const originUrlRows = ref(0)
// 防抖
const fd = (fn, delay) => {
  let timer = null
  return function (url) {
    if (timer) {
      clearTimeout(timer)
      timer = null
    }
    timer = setTimeout(() => {
      fn(url)
    }, delay)
  }
}
const isLoading = ref(false)
const queryTitle = (url) => {
  if (reg.test(url)) {
    isLoading.value = true
    API.smallLinkPage.queryTitle({ url: url }).then((res) => {
      formData.describe = res?.data?.data
      isLoading.value = false
    })
  }
}
const getTitle = fd(queryTitle, 1000)
watch(
  () => formData.originUrl,
  (nV) => {
    originUrlRows.value = (nV || '').split(/\r|\r\n|\n/)?.length ?? 0
    // 只有在描述内容为空时才会去查询链接对应的标题
    if (!formData.describe) {
      // 外边包一层防抖
      getTitle(nV)
    }
  }
)
const maxDescribeRows = ref(100) // 最多多少行
// 描述信息有多少行
const describeRows = ref(0)
watch(
  () => formData.describe,
  (nV) => {
    describeRows.value = (nV || '').split(/\r|\r\n|\n/)?.length ?? 0
  }
)

// 将分组数据传给选择分组选项并默认选中第一项
watch(
  () => props.groupInfo,
  (nV) => {
    groupInfo.value = nV
    // console.log('默认的gid', props.defaultGid)
    if (props.defaultGid) {
      formData.gid = props.defaultGid
    } else {
      formData.gid = nV[0].gid
    }
  },
  {
    immediate: true
  }
)
watch(
  () => props.defaultGid,
  (nV) => {
    if (props.defaultGid) {
      formData.gid = props.defaultGid
    } else {
      formData.gid = nV[0].gid
    }
  },
  {
    immediate: true
  }
)

// 校验规则
const formRule = reactive({
  originUrl: [
    { required: true, message: '请输入链接', trigger: 'blur' },
    {
      validator: function (rule, value, callback) {
        // console.log('============', value, value.split('/n'))
        if (value) {
          value.split(/\r|\r\n|\n/).forEach((item) => {
            if (!reg.test(item)) {
              callback(new Error('请输入 http:// 或 https:// 开头的链接或应用跳转链接'))
            }
          })
        }
        if (originUrlRows.value > maxOriginUrlRows.value) {
          callback(new Error('超过输入' + maxOriginUrlRows.value + '行'))
        } else {
          callback()
          submitDisable.value = false
        }
      },
      trigger: 'blur'
    }
  ],
  gid: [{ required: true, message: '请选择分组', trigger: 'blur' }],
  describe: [
    { required: true, message: '请输入描述信息', trigger: 'blur' },
    {
      validator: function (rule, value, callback) {
        // console.log('============', value, value.split('/n'))
        if (props.isSingle === false && describeRows.value !== originUrlRows.value) {
          callback(new Error('标题数量与链接数量不等'))
        }
        if (describeRows.value > maxDescribeRows.value) {
          callback(new Error('超过输入' + maxDescribeRows.value + '行'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ],
  validDate: [
    { required: false, message: '请输日期', trigger: 'blur' }
    // {
    //   validator: function (rule, value, callback) {
    //     if (describeRows.value > maxDescribeRows.value) {
    //       callback(new Error('超过输入' + maxDescribeRows.value + '行'))
    //     } else {
    //       callback()
    //     }
    //   },
    //   trigger: 'blur'
    // }
  ]
})
// 限制日期选择器选择过去的时间
const disabledDate = (time) => {
  return new Date(time).getTime() < new Date().getTime() //选当前时间之后的时间
}

// 将组件里面的确认和取消点击事件传出去
const emits = defineEmits(['onSubmit', 'cancel'])
// 点击确定按钮后的校验
const ruleFormRef = ref()
const submitDisable = ref(false)
const onSubmit = async (formEl) => {
  submitDisable.value = true
  if (!formEl) {
    submitDisable.value = false
    return
  }
  await formEl.validate(async (valid, fields) => {
    if (valid) {
      const res = await API.smallLinkPage.addSmallLink(formData)
      if (!res?.data?.success) {
        if (res?.data?.code === 'A000001') {
          ElMessage.warning({
            message: res.data.message,
            duration: 5000
          })
        } else {
          ElMessage.error(res.data.message)
        }
      } else {
        ElMessage.success('创建成功！')
        emits('onSubmit', false)
        submitDisable.value = false
      }
    } else {
      ElMessage.error('创建失败！')
    }
  })
}
const cancel = () => {
  emits('cancel', false)
  initFormData()
}
onBeforeUnmount(() => {
  initFormData()
})
defineExpose({
  // 当外部对话框以任何方式关闭时，初始化表单数据
  initFormData
})
</script>

<style lang="scss" scoped>
.create-link-form {
  padding: 0 12px;

  :deep(.el-form-item) {
    margin-bottom: 18px;
    
    &:last-child {
      margin-bottom: 0;
    }

    .el-form-item__label {
      font-size: 14px;
      color: #606266;
      padding-right: 12px;
    }

    .el-form-item__content {
      .el-input__wrapper {
        padding: 0 12px;
        height: 36px;
      }

      .el-textarea__inner {
        padding: 8px 12px;
      }
    }
  }
}

.custom-input {
  :deep(.el-input__wrapper) {
    background-color: #f5f7fa;
    border-radius: 4px;
    border: 1px solid transparent;
    transition: all 0.3s ease;
    
    &:hover, &.is-focus {
      border-color: #409eff;
      background-color: #fff;
    }
  }
}

.custom-select {
  width: 100%;
  
  :deep(.el-input__wrapper) {
    background-color: #f5f7fa;
    border-radius: 4px;
    border: 1px solid transparent;
    transition: all 0.3s ease;
    
    &:hover {
      border-color: #409eff;
      background-color: #fff;
    }
  }
}

.validity-item {
  margin-bottom: 16px;
  
  :deep(.el-radio-group) {
    .el-radio {
      margin-right: 20px;
      
      &:last-child {
        margin-right: 0;
      }
    }
  }
}

.date-picker-item {
  margin-bottom: 24px;
}

.date-picker-wrapper {
  display: flex;
  align-items: center;
  gap: 12px;
  
  .custom-date-picker {
    width: 240px;
  }
}

.row-count {
  display: block;
  margin-top: 4px;
  color: #909399;
  font-size: 12px;
}

.alert {
  color: #e6a23c;
  font-size: 13px;
  display: inline-flex;
  align-items: center;
  
  &::before {
    content: "!";
    display: inline-block;
    width: 16px;
    height: 16px;
    line-height: 16px;
    text-align: center;
    border-radius: 50%;
    background-color: #fdf6ec;
    color: #e6a23c;
    margin-right: 6px;
    font-weight: bold;
  }
}

.form-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding-top: 20px;
  margin-top: 20px;
  border-top: 1px solid #ebeef5;

  .el-button {
    min-width: 88px;
    height: 36px;
    padding: 0 20px;
    font-weight: 500;
    border-radius: 4px;
    transition: all 0.3s ease;
    
    &:hover {
      transform: translateY(-1px);
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    }
    
    &.is-disabled {
      transform: none;
      box-shadow: none;
    }
  }
}
</style>
