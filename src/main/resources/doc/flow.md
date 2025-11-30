# **多区域（HK / SG）Git 分支与发布管理规范**

**适用场景：多地区 HK / SG 并行开发 + 多环境 Dev/SIT/UAT/Prod 管理**

---

# 1.  **目标（为什么需要这套规范）**

本规范解决以下核心问题：

* HK 与 SG **同时开发不同功能**，互不影响
* HK 卡住审批/合规时，SG **仍可自由上线**
* SG 大改在 dev 测试中，HK **仍可发紧急 release**
* master 保持“**真实上线版本**”，避免未上线代码污染
* 各环境（hk-dev / sg-dev / sit / uat / prod）**对应分支清晰**
* release 修复的 bug **不会丢**
* 代码流向统一、有章可循，可溯源、可回滚

这套方案适用于：
**多区域、多业务线、多环境、多功能并行开发场景。**

---

# 2.  **分支结构**

本项目采用**多区域 + 多 release + 按迭代刷新 dev 分支**的方式。

## 2.1 总分支结构

| 分支                   | 作用                         | 生命周期 | 上线关系      |
|----------------------| -------------------------- | ---- | --------- |
| **master**           | 已上线的稳定基线（Prod 真相）          | 永久   | 仅上线成功后更新  |
| **dev-hk**           | HK 当前迭代集成 / Dev 测试         | 一个迭代 | 不会影响 master |
| **dev-sg**           | SG 当前迭代集成 / Dev 测试         | 一个迭代 | 不会影响 master |
| **feature/hk-***     | HK 功能开发分支                  | 功能周期 | 合进 dev-hk |
| **feature/sg-***     | SG 功能开发分支                  | 功能周期 | 合进 dev-sg |
| **release/hk-x.y.z** | HK 发布版本分支（SIT/UAT/Prod 共用） | 单次发布 | 上线成功后合 master |
| **release/sg-x.y.z** | SG 发布版本分支                  | 单次发布 | 上线成功后合 master |
| **bugfix/xxx**       | SIT/UAT/Prod 紧急修复          | 临时   | 修完后合回 release |

---

# 3. **分支角色与规则说明**

## 3.1 **master：线上真实世界**

* 只包含**已经在生产环境成功上线**的代码
* 不允许提前合尚未上线的 release
* 对应每一次正式 prod 的 tag，例如：

    * `hk-1.2.0-prod`
    * `sg-2.0.0-prod`

---

## 3.2 **dev-hk / dev-sg（区域 Dev 环境分支）**

特点：

* 每个区域一个 dev 集成分支
* 每个迭代一个新的 dev 分支（如 `dev-hk-2025Q1`）
* dev 环境部署对应 dev 分支（hk-dev / sg-dev）

用途：

* 区域多人协作的开发集成
* 功能合并后在 dev 环境进行冒烟、集成、联调
* 从 release 修的 bugfix 必须同步回 dev 分支

不作为上线基线。

---

## 3.3 **feature 分支**

命名规范：

* HK 功能：`feature/hk-<功能名>`
* SG 功能：`feature/sg-<功能名>`

规则：

* 必须从区域 dev 分支切出：
  `dev-hk → feature/hk-xxx`
  `dev-sg → feature/sg-xxx`
* 完成功能后合回 dev 分支
* 支持 rebase / squash（只在 feature 内部）

---

## 3.4 **release 分支（最重要）**

示例：

* `release/hk-1.2.0`
* `release/sg-2.0.0`

特点：

* 从 **master** 切出（保持干净）
* 把本次上线涉及的 feature 合进 release
* SIT / UAT / PROD 全程使用同一个 release 分支
* 该分支上的 bugfix 影响当前发布版本

上线后：

* 必须 merge 回 master
* 可删除 release 分支（保留 tag）

---

# 4. **开发 & 发布流程（核心部分）**

## （一）区域开发流程（HK 示例）

以下步骤 SG 也完全相同。

### Step 1：开始新迭代 → 切 dev 分支

```bash
git checkout master
git checkout -b dev-hk-2025Q1
git push -u origin dev-hk-2025Q1
```

### Step 2：功能开发 → 切 feature 分支

```bash
git checkout dev-hk-2025Q1
git checkout -b feature/hk-fee-rule
```

### Step 3：功能完成后 → 合回 dev-hk-2025Q1

```bash
git checkout dev-hk-2025Q1
git merge --no-ff feature/hk-fee-rule
git push
```

HK-dev 环境自动部署最新的 `dev-hk-2025Q1`。

### Step 4：多个 HK 功能在 dev-hk 集成

团队成员都把自己的 feature 合进 dev-hk，形成第一个“区域整体版本”。

---

## （二）某区域准备发布（HK Q1 ）

### Step 1：从 master 切 release

```bash
git checkout master
git pull
git checkout -b release/hk-1.2.0
git push -u origin release/hk-1.2.0
```

### Step 2：将本次要上的功能从 feature 合入 release

```bash
git checkout release/hk-1.2.0
git merge --no-ff feature/hk-fee-rule
git merge --no-ff feature/hk-new-kyc-flow
git push
```

### Step 3：SIT/UAT 全程使用 release/hk-1.2.0

* hk-dev（可选）：部署 release
* hk-sit：部署 release
* hk-uat：部署 release

保持测试链条一致。

---

## （三）SIT / UAT 测试发现 bug → 如何修？

### Step 1：在 release 修 bug

```bash
git checkout release/hk-1.2.0
git checkout -b hotfix/hk-1.2.0-bug-001
# 修复
git checkout release/hk-1.2.0
git merge --no-ff hotfix/hk-1.2.0-bug-001
git push
```

### Step 2：同步 bugfix → dev-hk（必要）

> 必须合回 dev-hk，确保下轮开发不再出现旧 bug。

```bash
git checkout dev-hk-2025Q1
git merge --no-ff release/hk-1.2.0
git push
```

### Step 3：不能提前合 master

因为 release 还没上线。

---

## （四）HK 上 Production

1. 在 release 打 prod tag：

   ```
   git tag hk-1.2.0-prod
   git push --tags
   ```

2. 上线成功后：

```bash
git checkout master
git merge --no-ff release/hk-1.2.0
git push
```

3. master 现在正式 = HK 1.2.0 上线版本。

---

# 5. 多区域（HK vs SG）如何互不干扰？

假设：

* HK 做大改（半年上线不了）
* SG 每两周一个小版本

只要遵守以下原则，两区不会互相污染：

### ✔ release 分支永远从 master 切

如果 HK 尚未上线，HK 功能不会进 master。

SG 切 release from master → **不会带 HK 代码**。

### ✔ dev-hk / dev-sg 互相隔离

HK 的大改只在 dev-hk
SG的小版本只在 dev-sg

### ✔ release bugfix → 合回 dev，而不是 master

master 永远保持“已上线版本”

### ✔ 如需共用的 bugfix → 从对方 release cherry-pick

避免污染 master。

---

# 6. CI/CD 环境分支映射（最重要的配置表）

| 环境       | HK 部署分支             | SG 部署分支             |
| -------- | ------------------- | ------------------- |
| **dev**  | `dev-hk`       | `dev-sg`       |
| **sit**  | `release/hk-x.y.z`  | `release/sg-x.y.z`  |
| **uat**  | `release/hk-x.y.z`  | `release/sg-x.y.z`  |
| **prod** | tag：`hk-x.y.z-prod` | tag：`sg-x.y.z-prod` |

**强制约束：**

*  prod 不允许部署 master、dev、feature 分支
*  uat/sit 不允许部署 dev 或 feature
*  只能部署对应 release 分支 / tag

---

# 7. 分支保护与权限（必须执行）

### master（最强保护）

* 禁止直接 push
* 只能通过 PR merge
* 需要至少 1–2 reviewer
* 需要 CI 测试通过

### release/*

* 禁止直接 push
* 必须 PR merge
* 测试通过后才可合

### dev-hk / dev-sg

* 可允许直接 push
* 推荐 PR merge（方便 code review）

### feature/*

* 可自由 push / rebase

---

# 8. Tag 策略

上线 tag：

```
hk-1.2.0-prod
sg-2.0.0-prod
```

补丁 tag：

```
hk-1.2.1-prod
sg-2.0.1-prod
```

优点：

* 版本可追踪
* 回滚简单
* release notes 可自动生成

---

# 9. Hotfix 生产紧急修复流程

适合：HK prod 出现线上重大 bug（SG 同时在准备另一个发布）

流程：

1. 从 prod tag 切 hotfix：

```bash
git checkout hk-1.2.0-prod
git checkout -b release/hk-1.2.1
git checkout -b hotfix/hk-prod-bug-999
```

2. 修 bug → 合到对应 release（如 `release/hk-1.2.1`）
3. 部署 hk-prod（形成 `hk-1.2.1-prod`）
4. 修复同步到：

    * master（上线成功后）
    * dev-hk（必要时）
    * release/hk-1.3.0（如未来版本也要修）

不会影响 SG 任何内容。


