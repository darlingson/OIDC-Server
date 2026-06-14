import { createFileRoute } from '@tanstack/react-router'

export const Route = createFileRoute('/dashboard/documentation')({
  component: RouteComponent,
})

function RouteComponent() {
  return <div>Hello "/dashboard/documentation"!</div>
}
