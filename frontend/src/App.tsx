import { Routes, Route } from 'react-router-dom'
import { Toaster } from '@/components/ui/toaster'
import ProtectedRoute from '@/components/ProtectedRoute'
import Layout from '@/components/Layout'
import { ErrorBoundary } from '@/components/ErrorBoundary'

// Pages
import WelcomePage from '@/pages/WelcomePage'
import LoginPage from '@/pages/LoginPage'
import RegisterPage from '@/pages/RegisterPage'
import DashboardPage from '@/pages/DashboardPage'
import TreePage from '@/pages/TreePage'
import PersonPage from '@/pages/PersonPage'
import PersonCreatePage from '@/pages/PersonCreatePage'
import AdminPage from '@/pages/AdminPage'
import NotFoundPage from '@/pages/NotFoundPage'

function App() {
  return (
    <ErrorBoundary>
      <Routes>
        {/* Public routes */}
        <Route path="/" element={<WelcomePage />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />

        {/* Protected routes */}
        <Route element={<ProtectedRoute><Layout /></ProtectedRoute>}>
          <Route path="/dashboard" element={<ErrorBoundary><DashboardPage /></ErrorBoundary>} />
          <Route path="/tree" element={<ErrorBoundary><TreePage /></ErrorBoundary>} />
          <Route path="/tree/:treeId" element={<ErrorBoundary><TreePage /></ErrorBoundary>} />
          <Route path="/tree/:treeId/person/new" element={<ErrorBoundary><PersonCreatePage /></ErrorBoundary>} />
          <Route path="/person/:personId" element={<ErrorBoundary><PersonPage /></ErrorBoundary>} />
        </Route>

        {/* Admin routes */}
        <Route element={<ProtectedRoute requireAdmin><Layout /></ProtectedRoute>}>
          <Route path="/admin" element={<ErrorBoundary><AdminPage /></ErrorBoundary>} />
        </Route>

        {/* 404 */}
        <Route path="*" element={<NotFoundPage />} />
      </Routes>
      <Toaster />
    </ErrorBoundary>
  )
}

export default App
