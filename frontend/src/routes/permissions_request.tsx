import { createFileRoute } from '@tanstack/react-router'

export const Route = createFileRoute('/permissions_request')({
  component: RouteComponent,
})

function RouteComponent() {
  return <div>Hello "/permissions_request"!</div>
}
