import { createFileRoute } from '@tanstack/react-router'

export const Route = createFileRoute('/dashboard/audit_logs')({
  component: RouteComponent,
})

function RouteComponent() {
  return <div>Hello "/dashboard/audit_logs"!</div>
}
