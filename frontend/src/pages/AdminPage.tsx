import { useState } from 'react'
import { useTranslation } from 'react-i18next'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { adminApi, type AdminUser, type AuditLog } from '@/api/admin'
import { Button } from '@/components/ui/button'

type Tab = 'users' | 'audit'

export default function AdminPage() {
  const { t } = useTranslation()
  const [activeTab, setActiveTab] = useState<Tab>('users')

  return (
    <div className="p-6">
      <div className="mb-6">
        <h1 className="text-3xl font-bold text-foreground">
          {t('admin.title', 'Admin Panel')}
        </h1>
        <p className="mt-2 text-muted-foreground">
          {t('admin.description', 'Manage users and view system activity')}
        </p>
      </div>

      {/* Stats */}
      <AdminStats />

      {/* Tabs */}
      <div className="border-b border-border mb-6">
        <nav className="-mb-px flex space-x-8">
          <button
            onClick={() => setActiveTab('users')}
            className={`py-4 px-1 border-b-2 font-medium text-sm ${
              activeTab === 'users'
                ? 'border-primary text-primary'
                : 'border-transparent text-muted-foreground hover:text-foreground hover:border-border'
            }`}
          >
            {t('admin.users', 'Users')}
          </button>
          <button
            onClick={() => setActiveTab('audit')}
            className={`py-4 px-1 border-b-2 font-medium text-sm ${
              activeTab === 'audit'
                ? 'border-primary text-primary'
                : 'border-transparent text-muted-foreground hover:text-foreground hover:border-border'
            }`}
          >
            {t('admin.auditLogs', 'Audit Logs')}
          </button>
        </nav>
      </div>

      {/* Tab Content */}
      {activeTab === 'users' && <UsersTab />}
      {activeTab === 'audit' && <AuditLogsTab />}
    </div>
  )
}

function AdminStats() {
  const { t } = useTranslation()

  const { data: stats } = useQuery({
    queryKey: ['admin', 'stats'],
    queryFn: () => adminApi.getStats(),
  })

  return (
    <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-6">
      <div className="bg-card rounded-lg shadow border border-border p-4">
        <div className="text-sm font-medium text-muted-foreground">
          {t('admin.pendingApprovals', 'Pending Approvals')}
        </div>
        <div className="mt-1 text-3xl font-semibold text-amber-400">
          {stats?.pendingApprovals ?? '-'}
        </div>
      </div>
    </div>
  )
}

function UsersTab() {
  const { t } = useTranslation()
  const queryClient = useQueryClient()
  const [page, setPage] = useState(0)

  const { data: usersPage, isLoading } = useQuery({
    queryKey: ['admin', 'users', page],
    queryFn: () => adminApi.getUsers(page),
  })

  const approveMutation = useMutation({
    mutationFn: adminApi.approveUser,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['admin'] })
    },
  })

  const suspendMutation = useMutation({
    mutationFn: adminApi.suspendUser,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['admin'] })
    },
  })

  const deleteMutation = useMutation({
    mutationFn: adminApi.deleteUser,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['admin'] })
    },
  })

  if (isLoading) {
    return <div className="text-muted-foreground">{t('common.loading', 'Loading...')}</div>
  }

  const users = usersPage?.content || []

  if (users.length === 0) {
    return (
      <div className="text-center py-12 text-muted-foreground">
        {t('admin.noUsers', 'No users found')}
      </div>
    )
  }

  return (
    <div className="bg-card shadow rounded-lg overflow-hidden border border-border">
      <table className="min-w-full divide-y divide-border">
        <thead className="bg-secondary">
          <tr>
            <th className="px-6 py-3 text-left text-xs font-medium text-muted-foreground uppercase tracking-wider">
              {t('admin.user', 'User')}
            </th>
            <th className="px-6 py-3 text-left text-xs font-medium text-muted-foreground uppercase tracking-wider">
              {t('admin.role', 'Role')}
            </th>
            <th className="px-6 py-3 text-left text-xs font-medium text-muted-foreground uppercase tracking-wider">
              {t('admin.status', 'Status')}
            </th>
            <th className="px-6 py-3 text-left text-xs font-medium text-muted-foreground uppercase tracking-wider">
              {t('admin.registered', 'Registered')}
            </th>
            <th className="px-6 py-3 text-right text-xs font-medium text-muted-foreground uppercase tracking-wider">
              {t('admin.actions', 'Actions')}
            </th>
          </tr>
        </thead>
        <tbody className="bg-card divide-y divide-border">
          {users.map((user) => (
            <UserRow
              key={user.id}
              user={user}
              onApprove={() => approveMutation.mutate(user.id)}
              onSuspend={() => suspendMutation.mutate(user.id)}
              onDelete={() => {
                if (confirm(t('admin.confirmDelete', 'Are you sure you want to delete this user?'))) {
                  deleteMutation.mutate(user.id)
                }
              }}
              isLoading={
                approveMutation.isPending ||
                suspendMutation.isPending ||
                deleteMutation.isPending
              }
            />
          ))}
        </tbody>
      </table>

      {/* Pagination */}
      {usersPage && usersPage.totalPages > 1 && (
        <div className="bg-card px-4 py-3 flex items-center justify-between border-t border-border">
          <div className="text-sm text-muted-foreground">
            Page {page + 1} of {usersPage.totalPages}
          </div>
          <div className="flex gap-2">
            <Button
              variant="outline"
              size="sm"
              onClick={() => setPage((p) => Math.max(0, p - 1))}
              disabled={page === 0}
            >
              Previous
            </Button>
            <Button
              variant="outline"
              size="sm"
              onClick={() => setPage((p) => p + 1)}
              disabled={usersPage.last}
            >
              Next
            </Button>
          </div>
        </div>
      )}
    </div>
  )
}

function UserRow({
  user,
  onApprove,
  onSuspend,
  onDelete,
  isLoading,
}: {
  user: AdminUser
  onApprove: () => void
  onSuspend: () => void
  onDelete: () => void
  isLoading: boolean
}) {
  const { t } = useTranslation()

  const statusColors: Record<string, string> = {
    PENDING_APPROVAL: 'bg-amber-900/30 text-amber-400',
    ACTIVE: 'bg-green-900/30 text-green-400',
    SUSPENDED: 'bg-red-900/30 text-red-400',
  }

  return (
    <tr className="hover:bg-secondary/50">
      <td className="px-6 py-4 whitespace-nowrap">
        <div>
          <div className="text-sm font-medium text-foreground">
            {user.displayName}
          </div>
          <div className="text-sm text-muted-foreground">{user.email}</div>
        </div>
      </td>
      <td className="px-6 py-4 whitespace-nowrap">
        <span className="text-sm text-foreground">{user.role}</span>
      </td>
      <td className="px-6 py-4 whitespace-nowrap">
        <span
          className={`px-2 inline-flex text-xs leading-5 font-semibold rounded-full ${
            statusColors[user.status] || 'bg-secondary text-muted-foreground'
          }`}
        >
          {user.status.replace('_', ' ')}
        </span>
      </td>
      <td className="px-6 py-4 whitespace-nowrap text-sm text-muted-foreground">
        {new Date(user.createdAt).toLocaleDateString()}
      </td>
      <td className="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
        <div className="flex justify-end gap-2">
          {user.status === 'PENDING_APPROVAL' && (
            <Button
              size="sm"
              onClick={onApprove}
              disabled={isLoading}
              className="bg-green-600 hover:bg-green-700 text-white"
            >
              {t('admin.approve', 'Approve')}
            </Button>
          )}
          {user.status === 'ACTIVE' && (
            <Button
              size="sm"
              variant="outline"
              onClick={onSuspend}
              disabled={isLoading}
              className="text-amber-400 border-amber-400 hover:bg-amber-900/20"
            >
              {t('admin.suspend', 'Suspend')}
            </Button>
          )}
          {user.status === 'SUSPENDED' && (
            <Button
              size="sm"
              onClick={onApprove}
              disabled={isLoading}
              className="bg-green-600 hover:bg-green-700 text-white"
            >
              {t('admin.reactivate', 'Reactivate')}
            </Button>
          )}
          <Button
            size="sm"
            variant="outline"
            onClick={onDelete}
            disabled={isLoading}
            className="text-destructive border-destructive hover:bg-destructive/10"
          >
            {t('admin.delete', 'Delete')}
          </Button>
        </div>
      </td>
    </tr>
  )
}

function AuditLogsTab() {
  const { t } = useTranslation()
  const [page, setPage] = useState(0)

  const { data: logsPage, isLoading } = useQuery({
    queryKey: ['admin', 'audit-logs', page],
    queryFn: () => adminApi.getAuditLogs(page),
  })

  if (isLoading) {
    return <div className="text-muted-foreground">{t('common.loading', 'Loading...')}</div>
  }

  const logs = logsPage?.content || []

  if (logs.length === 0) {
    return (
      <div className="text-center py-12 text-muted-foreground">
        {t('admin.noLogs', 'No audit logs found')}
      </div>
    )
  }

  return (
    <div className="bg-card shadow rounded-lg overflow-hidden border border-border">
      <table className="min-w-full divide-y divide-border">
        <thead className="bg-secondary">
          <tr>
            <th className="px-6 py-3 text-left text-xs font-medium text-muted-foreground uppercase tracking-wider">
              {t('admin.timestamp', 'Timestamp')}
            </th>
            <th className="px-6 py-3 text-left text-xs font-medium text-muted-foreground uppercase tracking-wider">
              {t('admin.user', 'User')}
            </th>
            <th className="px-6 py-3 text-left text-xs font-medium text-muted-foreground uppercase tracking-wider">
              {t('admin.action', 'Action')}
            </th>
            <th className="px-6 py-3 text-left text-xs font-medium text-muted-foreground uppercase tracking-wider">
              {t('admin.entity', 'Entity')}
            </th>
            <th className="px-6 py-3 text-left text-xs font-medium text-muted-foreground uppercase tracking-wider">
              {t('admin.ipAddress', 'IP Address')}
            </th>
          </tr>
        </thead>
        <tbody className="bg-card divide-y divide-border">
          {logs.map((log) => (
            <AuditLogRow key={log.id} log={log} />
          ))}
        </tbody>
      </table>

      {/* Pagination */}
      {logsPage && logsPage.totalPages > 1 && (
        <div className="bg-card px-4 py-3 flex items-center justify-between border-t border-border">
          <div className="text-sm text-muted-foreground">
            Page {page + 1} of {logsPage.totalPages}
          </div>
          <div className="flex gap-2">
            <Button
              variant="outline"
              size="sm"
              onClick={() => setPage((p) => Math.max(0, p - 1))}
              disabled={page === 0}
            >
              Previous
            </Button>
            <Button
              variant="outline"
              size="sm"
              onClick={() => setPage((p) => p + 1)}
              disabled={logsPage.last}
            >
              Next
            </Button>
          </div>
        </div>
      )}
    </div>
  )
}

function AuditLogRow({ log }: { log: AuditLog }) {
  return (
    <tr className="hover:bg-secondary/50">
      <td className="px-6 py-4 whitespace-nowrap text-sm text-muted-foreground">
        {new Date(log.timestamp).toLocaleString()}
      </td>
      <td className="px-6 py-4 whitespace-nowrap text-sm text-foreground">
        {log.userEmail}
      </td>
      <td className="px-6 py-4 whitespace-nowrap">
        <span className="px-2 py-1 text-xs font-medium bg-secondary text-muted-foreground rounded">
          {log.action}
        </span>
      </td>
      <td className="px-6 py-4 whitespace-nowrap text-sm text-muted-foreground">
        {log.entityType}
        {log.entityId && (
          <span className="text-xs text-muted-foreground/70 ml-1">
            ({log.entityId.substring(0, 8)}...)
          </span>
        )}
      </td>
      <td className="px-6 py-4 whitespace-nowrap text-sm text-muted-foreground">
        {log.ipAddress || '-'}
      </td>
    </tr>
  )
}
