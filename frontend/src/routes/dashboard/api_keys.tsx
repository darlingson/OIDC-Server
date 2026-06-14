import { createFileRoute } from '@tanstack/react-router'

export const Route = createFileRoute('/dashboard/api_keys')({
  component: RouteComponent,
})

function RouteComponent() {
  return <div>Hello "/dashboard/api_keys"!</div>
}
