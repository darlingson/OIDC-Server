import { createFileRoute } from '@tanstack/react-router'

export const Route = createFileRoute('/dashboard/applications/add_new')({
  component: RouteComponent,
})

function RouteComponent() {
  return <div>Hello "/dashboard/applications/add_new"!</div>
}
